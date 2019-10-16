package ffc.airsync.api.healthcare

import ffc.airsync.db.DatabaseDao
import ffc.airsync.persons
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.users
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.UploadSpliter
import ffc.airsync.utils.callApi
import ffc.airsync.utils.callApiNoReturn
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.isTempId
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit

class RetofitHealthCareApi : RetofitApi<HealthCareUrl>(HealthCareUrl::class.java), HealthCareApi {

    private val logger by lazy { getLogger(this) }
    override fun clearAndCreateHealthCare(
        healthCare: List<HealthCareService>,
        progressCallback: (Int) -> Unit
    ): List<HealthCareService> {
        val healthCareLastUpdate = arrayListOf<HealthCareService>()
        callApiNoReturn { restService.cleanHealthCare(orgId = organization.id, authkey = tokenBarer).execute() }

        return _createHealthCare(healthCare, healthCareLastUpdate, progressCallback)
    }

    override fun createHealthCare(
        healthCare: List<HealthCareService>,
        progressCallback: (Int) -> Unit
    ): List<HealthCareService> {
        val healthCareLastUpdate = arrayListOf<HealthCareService>()
        return _createHealthCare(healthCare, healthCareLastUpdate, progressCallback)
    }

    private fun _createHealthCare(
        healthCare: List<HealthCareService>,
        healthCareLastUpdate: ArrayList<HealthCareService>,
        progressCallback: (Int) -> Unit
    ): ArrayList<HealthCareService> {
        val fixSizeCake = 100
        val healthCareSize = healthCare.size / fixSizeCake
        logger.info("Create health care total ${healthCare.size}")
        UploadSpliter.upload(fixSizeCake, healthCare) { it, index ->

            val result = callApi {
                restService.unConfirmHealthCareBlock(
                    orgId = organization.id,
                    authkey = tokenBarer,
                    block = index
                ).execute()

                val temp = restService.insertHealthCareBlock(
                    orgId = organization.id,
                    authkey = tokenBarer,
                    healthCare = it,
                    block = index
                )
                val respond = temp.execute()

                if (respond.code() == 201 || respond.code() == 200) {
                    restService.confirmHealthCareBlock(
                        orgId = organization.id,
                        authkey = tokenBarer,
                        block = index
                    ).execute()
                    respond.body() ?: arrayListOf()
                } else {
                    val message = "Error Loop ${respond.code()} ${respond.errorBody()?.charStream()?.readText()}"
                    throw ApiLoopException(message)
                }
            }
            healthCareLastUpdate.addAll(result)
            if (healthCareSize != 0) // fix bug หากยังไม่มี visit จะสร้างครั้งแรกจะถูกหารด้วย 0 จะ error
                progressCallback(((index * 50) / healthCareSize) + 50)
        }
        return healthCareLastUpdate
    }

    override fun syncHealthCareFromCloud(id: String, dao: DatabaseDao) {
        logger.info { "Sync from object id:$id" }
        val data = restService.getHomeVisit(orgId = organization.id, authkey = tokenBarer, id = id).execute()

        if (data.code() != 200) {
            logger.error("Not success get healthcare code=${data.code()}")
            return
        }
        val healthCareService = data.body()!!
        if (healthCareService.link?.isSynced == true) return
        val pcucode = pcucode

        val patientId = healthCareService.patientId
        val providerId = healthCareService.providerId

        if (patientId.isTempId() || providerId.isTempId()) {
            val ex = IllegalAccessException("Health Care Service provider or patient isTempId ")
            logger.error(ex) { ex.message }
            throw ex
        }

        val patient = persons.find { it.id == patientId }
        val provider = users.find { it.id == providerId }

        if (patient == null || provider == null) {
            logger.error {
                "ข้อมูลที่ใช้ประกอบการ visit ไม่ครบ " +
                        "ผู้ให้บริการ $providerId:${provider?.name} " +
                        "ผู้ถูกเยี่ยม $patientId:${patient?.name}"
            }
            return
        }

        logger.debug("partian id ${(patient.link!!.keys["pid"] as String).toLong()}")


        if (healthCareService.link!!.keys.isEmpty()) {
            healthCareService.communityServices.forEach {
                if (it is HomeVisit) {
                    dao.createHomeVisit(
                        it,
                        healthCareService,
                        pcucode,
                        pcucode,
                        patient,
                        provider.name
                    )
                }
            }
        } else {
            healthCareService.communityServices.forEach {
                if (it is HomeVisit) {
                    dao.updateHomeVisit(
                        it,
                        healthCareService,
                        pcucode,
                        pcucode,
                        patient,
                        provider.name
                    )
                }
            }
        }

        val result = updateHealthCare(healthCareService)

        logger.info { "Result healthcare from cloud $result" }
    }

    override fun updateHealthCare(healthCareService: HealthCareService): HealthCareService {
        val result = callApi {
            restService.updateHomeVisit(
                organization.id, tokenBarer,
                healthCareService.id, healthCareService
            ).execute()
        }.body()
        return result!!
    }
}

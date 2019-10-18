package ffc.airsync.api.healthcare

import ffc.airsync.api.user.syncJToCloud
import ffc.airsync.db.DatabaseDao
import ffc.airsync.gui
import ffc.airsync.persons
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.ui.AirSyncGUI.MESSAGE_TYPE.ERROR
import ffc.airsync.ui.AirSyncGUI.MESSAGE_TYPE.INFO
import ffc.airsync.users
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.UploadSpliter
import ffc.airsync.utils.callApi
import ffc.airsync.utils.callApiNoReturn
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.isTempId
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit

class HealthCareServiceApi : RetofitApi<HealthCareServiceUrl>(HealthCareServiceUrl::class.java), HealthCareApi {

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

        val provider = users.find { it.id == providerId } ?: {
            gui.createMessageDelay("พบผู้ใช้ใหม่กำลัง Sync...", delay = 5000)
            users.syncJToCloud()
            users.find { it.id == providerId }
        }.invoke()

        val patient = persons.find { it.id == patientId }

        if (patient == null || provider == null) {
            val message = "ข้อมูลที่ใช้ประกอบการ visit ไม่ครบ " +
                    "ผู้ให้บริการ $providerId:${provider?.name} " +
                    "ผู้ถูกเยี่ยม $patientId:${patient?.name}"
            logger.error {
                message
            }
            gui.createMessageDelay(message, ERROR, 5000)
            return
        }

        logger.debug("partian id ${(patient.link!!.keys["pid"] as String).toLong()}")

        if (healthCareService.link!!.keys.isEmpty()) {
            gui.createMessageDelay("เจ้าหน้าที่ ${provider.name} กำลังเยี่ยม ${patient.name}", INFO, 10000)
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
            gui.createMessageDelay("เจ้าหน้าที่ ${provider.name} อัพเดทข้อมูลการเยี่ยม ${patient.name}", INFO, 5000)
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

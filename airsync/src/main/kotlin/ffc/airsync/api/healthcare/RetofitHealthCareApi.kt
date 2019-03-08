package ffc.airsync.api.healthcare

import ffc.airsync.api.person.persons
import ffc.airsync.api.user.users
import ffc.airsync.db.DatabaseDao
import ffc.airsync.printDebug
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.UploadSpliter
import ffc.airsync.utils.callApi
import ffc.airsync.utils.callApiNoReturn
import ffc.airsync.utils.isTempId
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit

class RetofitHealthCareApi : RetofitApi<HealthCareUrl>(HealthCareUrl::class.java), HealthCareApi {

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
        val healthCareSize = healthCare.size
        UploadSpliter.upload(100, healthCare) { it, index ->

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
            progressCallback(((index * 50) / healthCareSize) + 50)
        }
        return healthCareLastUpdate
    }

    override fun syncHealthCareFromCloud(id: String, dao: DatabaseDao) {
        val data = restService.getHomeVisit(orgId = organization.id, authkey = tokenBarer, id = id).execute()

        if (data.code() != 200) {
            printDebug("Not success get healthcare code=${data.code()}")
            return
        }
        val healthCareService = data.body()!!
        if (healthCareService.link?.isSynced == true) return
        val pcucode = pcucode

        if (healthCareService.patientId.isTempId() ||
            healthCareService.providerId.isTempId()
        )
            throw IllegalAccessException(
                "Health Care Service provider or patient isTempId "
            )

        val patient = persons.find {
            it.id == healthCareService.patientId
        }!!

        printDebug("partian id ${(patient.link!!.keys["pid"] as String).toLong()}")

        val provider = users.find {
            it.id == healthCareService.providerId
        }!!

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

        printDebug("Result healthcare from cloud $result")
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

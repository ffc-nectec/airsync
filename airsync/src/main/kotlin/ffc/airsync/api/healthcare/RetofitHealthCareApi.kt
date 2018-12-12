package ffc.airsync.api.healthcare

import ffc.airsync.db.DatabaseDao
import ffc.airsync.persons
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.users
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.UploadSpliter
import ffc.airsync.utils.callApi
import ffc.airsync.utils.callApiNoReturn
import ffc.airsync.utils.isTempId
import ffc.airsync.utils.printDebug
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit

class RetofitHealthCareApi : RetofitApi<HealthCareUrl>(HealthCareUrl::class.java), HealthCareApi {

    override fun createHealthCare(healthCare: List<HealthCareService>): List<HealthCareService> {
        val healthCareLastUpdate = arrayListOf<HealthCareService>()
        callApiNoReturn { restService.cleanHealthCare(orgId = organization.id, authkey = tokenBarer).execute() }

        UploadSpliter.upload(200, healthCare) { it, index ->

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
            }
        }

        callApi {
            val result = restService.updateHomeVisit(
                organization.id, tokenBarer,
                healthCareService.id, healthCareService
            ).execute()
        }
    }
}

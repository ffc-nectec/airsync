package ffc.airsync.api.healthcare

import ffc.airsync.db.DatabaseDao
import ffc.airsync.persons
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.users
import ffc.airsync.utils.isTempId
import ffc.airsync.utils.printDebug

class RetofitHealthCareApi : RetofitApi(), HealthCareApi {
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

        dao.createHomeVisit(
            healthCareService,
            pcucode,
            pcucode,
            patient,
            provider.name
        )
    }
}

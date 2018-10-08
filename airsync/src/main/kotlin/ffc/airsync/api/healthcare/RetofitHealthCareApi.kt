package ffc.airsync.api.healthcare

import ffc.airsync.db.DatabaseDao
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.isTempId
import ffc.airsync.utils.persons
import ffc.airsync.utils.printDebug
import ffc.airsync.utils.users

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

        val patientHos = dao.findPerson(pcucode, (patient.link!!.keys["pid"] as String).toLong())
        patient.bundle.putAll(patientHos.bundle)

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

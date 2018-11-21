package ffc.airsync.api.healthcare

import ffc.airsync.Main
import ffc.airsync.healthCareApi
import ffc.airsync.persons
import ffc.airsync.users
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.healthcare.HealthCareService

fun ArrayList<HealthCareService>.initSync() {
    val localHomeVisit = arrayListOf<HealthCareService>().apply {
        addAll(load())
    }

    if (localHomeVisit.isEmpty()) {
        localHomeVisit.addAll(getHomeVisit())
        addAll(healthCareApi.createHomeVisit(localHomeVisit))
        save()
    } else {
        addAll(localHomeVisit)
    }
}

private fun getHomeVisit(): List<HealthCareService> {
    val dao = Main.instant.createDatabaseDao()

    return dao.getHealthCareService(users, persons)
}

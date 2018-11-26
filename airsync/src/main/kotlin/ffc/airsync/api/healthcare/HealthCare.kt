package ffc.airsync.api.healthcare

import ffc.airsync.Main
import ffc.airsync.healthCareApi
import ffc.airsync.persons
import ffc.airsync.users
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.healthcare.HealthCareService

fun ArrayList<HealthCareService>.initSync() {
    val localHealthCare = arrayListOf<HealthCareService>().apply {
        addAll(load())
    }

    if (localHealthCare.isEmpty()) {
        val temp = listOf<HealthCareService>().load("healthTemp.json")
        if (temp.isEmpty()) {
            localHealthCare.addAll(getHealthCare())
            localHealthCare.save("healthTemp.json")
        }
        localHealthCare.addAll(temp)
        addAll(healthCareApi.createHealthCare(localHealthCare))
        save()
    } else {
        addAll(localHealthCare)
    }
}

private fun getHealthCare(): List<HealthCareService> {
    return Main.instant.createDatabaseDao().getHealthCareService(
        user = users,
        person = persons
    )
}

package ffc.airsync.api.healthcare

import ffc.airsync.Main
import ffc.airsync.healthCareApi
import ffc.airsync.homeHealthTypeApi
import ffc.airsync.icd10Api
import ffc.airsync.persons
import ffc.airsync.specialPpApi
import ffc.airsync.users
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.healthcare.HealthCareService

fun ArrayList<HealthCareService>.initSync() {
    val localHealthCare = arrayListOf<HealthCareService>().apply {
        addAll(load())
    }

    if (localHealthCare.isEmpty()) {
        // localHealthCare.addAll(getHealthCare())
        // localHealthCare.save("healthTemp.json")
        localHealthCare.clear()
        localHealthCare.addAll(listOf<HealthCareService>().load("healthTemp.json"))
        addAll(healthCareApi.createHealthCare(localHealthCare))
        save()
    } else {
        addAll(localHealthCare)
    }
}

private fun getHealthCare(): List<HealthCareService> {
    return Main.instant.createDatabaseDao().getHealthCareService(
        user = users,
        person = persons,
        lookupHealthType = { homeHealthTypeApi.lookup(it) },
        lookupICD10 = { icd10Api.lookup(it) },
        lookupSpecial = { specialPpApi.lookup(it) }
    )
}

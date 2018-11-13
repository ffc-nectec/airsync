package ffc.airsync.api.healthcare

import ffc.airsync.Main
import ffc.airsync.diseaseApi
import ffc.airsync.healthCareApi
import ffc.airsync.homeHealthTypeApi
import ffc.airsync.persons
import ffc.airsync.users
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.healthcare.HomeVisit

fun ArrayList<HomeVisit>.initSync() {
    val localHomeVisit = arrayListOf<HomeVisit>().apply {
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

private fun getHomeVisit(): List<HomeVisit> {
    val dao = Main.instant.createDatabaseDao()

    val lookupDisease = { icd10: String ->
        diseaseApi.lookup(icd10.trim()).first()
    }
    val lookupHomeHealthType = { typeId: String ->
        homeHealthTypeApi.lookup(typeId.trim()).first()
    }
    return dao.getHomeVisit(users, persons, lookupDisease, lookupHomeHealthType)
}

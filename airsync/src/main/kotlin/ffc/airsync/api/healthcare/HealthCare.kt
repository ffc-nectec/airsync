package ffc.airsync.api.healthcare

import ffc.airsync.Main
import ffc.airsync.lookupDisease
import ffc.airsync.lookupPersonId
import ffc.airsync.lookupServiceType
import ffc.airsync.lookupSpecialPP
import ffc.airsync.lookupUserId
import ffc.airsync.utils.load
import ffc.entity.healthcare.HealthCareService

fun ArrayList<HealthCareService>.initSync(progressCallback: (Int) -> Unit) {
    val cacheFile = arrayListOf<HealthCareService>().apply {
        addAll(load())
    }

    val sync = SyncHealthCare(Main.instant.dao)
    if (cacheFile.isEmpty()) {
        val jhcisVisit = getHealthCareFromDb(progressCallback)
        sync.localToCloud(cacheFile, jhcisVisit, this, progressCallback)
    } else {
        addAll(cacheFile)
    }
    progressCallback(100)
}

private fun getHealthCareFromDb(progressCallback: (Int) -> Unit): List<HealthCareService> {
    /*return Main.instant.dao.getHealthCareService(
        lookupPatientId = { pid -> persons.find { it.link!!.keys["pid"] == pid }?.id ?: "" },
        lookupProviderId = { name -> (users.find { it.name == name } ?: users.last()).id }
    )*/

    return Main.instant.dao.getHealthCareService(
        lookupPatientId = lookupPersonId,
        lookupProviderId = lookupUserId,
        lookupDisease = lookupDisease,
        lookupServiceType = lookupServiceType,
        lookupSpecialPP = lookupSpecialPP,
        progressCallback = progressCallback
    )
}

private const val healthCareLock = "lock"

fun List<HealthCareService>.lock(f: () -> Unit) {
    synchronized(healthCareLock) {
        f()
    }
}

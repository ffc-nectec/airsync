package ffc.airsync

import ffc.airsync.api.healthcare.healthCareApi
import ffc.airsync.api.house.houseApi
import ffc.airsync.api.notification.notificationApi
import ffc.airsync.db.DatabaseDao
import ffc.airsync.provider.notificationModule
import ffc.entity.Person
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit
import ffc.entity.place.House

class SetupNotification(val dao: DatabaseDao) {

    init {
        setupNotificationHandlerFor()
    }

    private fun setupNotificationHandlerFor() {
        notificationModule().apply {
            onTokenChange { firebaseToken ->
                notificationApi.registerChannel(firebaseToken)
            }
            onReceiveDataUpdate { type, id ->
                syncFlow(type, id, dao)
            }
        }
    }
}

fun syncFlow(type: String, id: String, dao: DatabaseDao) {
    when (type) {
        House::class.java.simpleName -> houseApi.syncHouseFromCloud(id, dao)
        HealthCareService::class.java.simpleName -> healthCareApi.syncHealthCareFromCloud(id, dao)
        HomeVisit::class.java.simpleName -> healthCareApi.syncHealthCareFromCloud(id, dao)
        Person::class.java.simpleName -> {
        }
        else -> {
            printDebug("Sync else type:$type id:$id")
        }
    }
}

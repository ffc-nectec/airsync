package ffc.airsync

import ffc.airsync.db.DatabaseDao
import ffc.airsync.provider.notificationModule

class SetupNotification(val dao: DatabaseDao) {

    init {
        setupNotificationHandlerFor()
    }

    private fun setupNotificationHandlerFor() {
        notificationModule().apply {
            onTokenChange { firebaseToken ->
                notificationApi.putFirebaseToken(firebaseToken)
            }
            onReceiveDataUpdate { type, id ->
                when (type) {
                    "House" -> houseApi.syncHouseFromCloud(id, dao)
                    "HealthCare" -> healthCareApi.syncHealthCareFromCloud(id, dao)
                    else -> println("Not type house.")
                }
            }
        }
    }
}

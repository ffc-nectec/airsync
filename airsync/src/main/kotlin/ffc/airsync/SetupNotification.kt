package ffc.airsync

import ffc.airsync.api.healthcare.healthCareApi
import ffc.airsync.api.house.houseApi
import ffc.airsync.api.notification.notificationApi
import ffc.airsync.db.DatabaseDao
import ffc.airsync.provider.notificationModule
import ffc.airsync.utils.getLogger
import ffc.entity.Person
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit
import ffc.entity.place.House

private val logger by lazy { getLogger(SetupNotification::class) }

class SetupNotification(val dao: DatabaseDao) {

    init {
        setupNotificationHandlerFor()
    }

    private fun setupNotificationHandlerFor() {
        logger.info("Setup notification.")
        notificationModule().apply {
            onTokenChange { firebaseToken ->
                logger.debug("Firebase token change.")
                notificationApi.registerChannel(firebaseToken)
            }
            onReceiveDataUpdate { type, id ->
                logger.info("Receive data from firebase $type")
                logger.debug("Receive data id:$id")
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
            logger.debug("Sync else type:$type id:$id")
        }
    }
}

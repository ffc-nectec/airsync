/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package ffc.airsync

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
        House::class.java.simpleName -> houseApi.get(id)
        HealthCareService::class.java.simpleName -> healthCareApi.syncHealthCareFromCloud(id, dao)
        HomeVisit::class.java.simpleName -> healthCareApi.syncHealthCareFromCloud(id, dao)
        Person::class.java.simpleName -> {
        }
        else -> {
            logger.debug("Sync else type:$type id:$id")
        }
    }
}

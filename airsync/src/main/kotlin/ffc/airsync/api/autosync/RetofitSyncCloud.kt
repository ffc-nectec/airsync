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

package ffc.airsync.api.autosync

import ffc.airsync.db.DatabaseDao
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.syncFlow
import ffc.airsync.utils.callApi
import ffc.airsync.utils.getLogger
import ffc.entity.Entity

class RetofitSyncCloud : RetofitApi<SyncUrl>(SyncUrl::class.java), SyncCloud {
    override fun sync(dao: DatabaseDao) {
        val (syncList, responseCode) = getCloud()

        logger.info {
            var log = "Sync from cloud http return code:$responseCode "
            val counts = hashMapOf<String, Int>()
            syncList?.forEach {
                counts[it.type] =
                    if (counts[it.type] == null) 1
                    else counts[it.type]!! + 1
            }
            counts.forEach { (type, count) -> log += " $type=$count" }
            log
        }
        if (responseCode != 200 || syncList == null) {
            return
        }

        syncAll(syncList, dao)
    }

    private fun syncAll(syncList: List<Entity>, dao: DatabaseDao) {
        syncList.forEach {
            try {
                syncFlow(it.type, it.id, dao)
            } catch (ex: Exception) {
                logger.warn(ex.message ?: "", ex)
            }
        }
    }

    override fun syncFilter(dao: DatabaseDao, type: List<String>) {
        val (syncList, responseCode) = getCloud()
        if (responseCode != 200 || syncList == null) {
            return
        }
        val filter = syncList.filter { type.contains(it.type) }
        syncAll(filter, dao)
    }

    private fun getCloud(): Pair<List<Entity>?, Int> {
        val syncRespond = callApi { restService.syncData(organization.id, tokenBarer).execute() }
        val syncList = syncRespond.body()
        val responseCode = syncRespond.code()
        return Pair(syncList, responseCode)
    }

    companion object {
        private val logger by lazy { getLogger(this) }
    }
}

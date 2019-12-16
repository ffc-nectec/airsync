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

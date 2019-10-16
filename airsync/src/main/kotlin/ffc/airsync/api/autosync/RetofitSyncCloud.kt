package ffc.airsync.api.autosync

import ffc.airsync.db.DatabaseDao
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.syncFlow
import ffc.airsync.utils.callApi
import ffc.airsync.utils.getLogger

class RetofitSyncCloud(val outMessage: (String) -> Unit) : RetofitApi<SyncUrl>(SyncUrl::class.java), SyncCloud {
    override fun sync(dao: DatabaseDao) {
        val syncRespond = callApi { restService.syncData(organization.id, tokenBarer).execute() }
        val syncList = syncRespond.body()
        val responseCode = syncRespond.code()

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
            outMessage("")
            return
        }

        outMessage("ตรวจสอบข้อมูลจาก Cloud....")
        syncList.forEach {
            syncFlow(it.type, it.id, dao)
        }
        outMessage("")
    }

    companion object {
        private val logger by lazy { getLogger(this) }
    }
}

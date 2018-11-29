package ffc.airsync.api.autosync

import ffc.airsync.db.DatabaseDao
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.syncFlow

class RetofitSyncCloud : RetofitApi<SyncUrl>(SyncUrl::class.java), SyncCloud {
    override fun sync(dao: DatabaseDao) {
        val syncRespond = restService.syncData(organization.id, tokenBarer).execute()
        val syncList = syncRespond.body()

        if (syncRespond.code() != 200 || syncList == null) {
            return
        }

        syncList.forEach {
            syncFlow(it.type, it.id, dao)
        }
    }
}

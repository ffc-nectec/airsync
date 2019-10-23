package ffc.airsync.api.autosync

import ffc.airsync.db.DatabaseDao

interface SyncCloud {
    fun sync(dao: DatabaseDao)
    fun syncFilter(dao: DatabaseDao, type: List<String>)
}

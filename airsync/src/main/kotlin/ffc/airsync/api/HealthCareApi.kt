package ffc.airsync.api

import ffc.airsync.db.DatabaseDao

interface HealthCareApi {
    fun syncHealthCareFromCloud(id: String, dao: DatabaseDao)
}

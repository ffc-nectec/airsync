package ffc.airsync.api.healthcare

import ffc.airsync.db.DatabaseDao

interface HealthCareApi {
    fun syncHealthCareFromCloud(id: String, dao: DatabaseDao)
}

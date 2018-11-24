package ffc.airsync.api.healthcare

import ffc.airsync.db.DatabaseDao
import ffc.entity.healthcare.HealthCareService

interface HealthCareApi {
    fun syncHealthCareFromCloud(id: String, dao: DatabaseDao)
    fun createHealthCare(healthCare: List<HealthCareService>): List<HealthCareService>
}

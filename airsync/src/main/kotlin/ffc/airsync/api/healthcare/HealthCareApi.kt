package ffc.airsync.api.healthcare

import ffc.airsync.db.DatabaseDao
import ffc.entity.healthcare.HealthCareService

interface HealthCareApi {
    fun syncHealthCareFromCloud(id: String, dao: DatabaseDao)
    fun clearAndCreateHealthCare(
        healthCare: List<HealthCareService>,
        progressCallback: (Int) -> Unit,
        clearCloud: Boolean = true
    ): List<HealthCareService>

    fun createHealthCare(
        healthCare: List<HealthCareService>,
        progressCallback: (Int) -> Unit
    ): List<HealthCareService>

    fun updateHealthCare(healthCareService: HealthCareService): HealthCareService
}

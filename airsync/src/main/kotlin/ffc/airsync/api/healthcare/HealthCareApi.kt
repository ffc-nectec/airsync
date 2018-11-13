package ffc.airsync.api.healthcare

import ffc.airsync.db.DatabaseDao
import ffc.entity.healthcare.HomeVisit

interface HealthCareApi {
    fun syncHealthCareFromCloud(id: String, dao: DatabaseDao)
    fun createHomeVisit(homeVisit: List<HomeVisit>): List<HomeVisit>
}

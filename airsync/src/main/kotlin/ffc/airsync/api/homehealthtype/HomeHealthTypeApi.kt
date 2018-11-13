package ffc.airsync.api.homehealthtype

import ffc.entity.healthcare.CommunityServiceType

interface HomeHealthTypeApi {
    fun lookup(healthTypeId: String): List<CommunityServiceType>
}

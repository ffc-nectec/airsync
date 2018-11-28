package ffc.airsync.api.homehealthtype

import ffc.entity.healthcare.CommunityService.ServiceType

interface HomeHealthTypeApi {
    fun lookup(healthTypeId: String): ServiceType
}

val homeHealthTypeApi: HomeHealthTypeApi by lazy { RetofitHomeHealthTypeApi() }

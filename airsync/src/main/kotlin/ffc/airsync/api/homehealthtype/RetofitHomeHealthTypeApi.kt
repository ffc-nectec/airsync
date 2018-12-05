package ffc.airsync.api.homehealthtype

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi
import ffc.entity.healthcare.CommunityService.ServiceType

class RetofitHomeHealthTypeApi : RetofitApi<HomeHealtyTypeUrl>(HomeHealtyTypeUrl::class.java, 10240),
    HomeHealthTypeApi {
    override fun lookup(healthTypeId: String): ServiceType {
        return callApi {
            restService.lookupCommunityServiceType(
                authkey = tokenBarer,
                id = healthTypeId
            ).execute().body() ?: ServiceType(healthTypeId, "")
        }
    }
}

package ffc.airsync.api.homehealthtype

import ffc.airsync.retrofit.RetofitApi
import ffc.entity.healthcare.CommunityServiceType

class RetofitHomeHealthTypeApi : RetofitApi(), HomeHealthTypeApi {
    override fun lookup(healthTypeId: String): List<CommunityServiceType> {
        while (true) {

            try {
                val respond = restService.lookupCommunityServiceType(
                    authkey = tokenBarer,
                    query = healthTypeId
                ).execute()

                return (respond.body() ?: arrayListOf())
            } catch (ex: java.net.SocketTimeoutException) {
                ex.printStackTrace()
            }
        }
    }
}

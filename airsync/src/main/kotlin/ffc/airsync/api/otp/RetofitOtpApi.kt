package ffc.airsync.api.otp

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.callApi

class RetofitOtpApi : RetofitApi<OtpUrl>(OtpUrl::class.java), OtpApi {
    override fun get(): String {
        return callApi {
            val response = restService.get(
                orgId = organization.id,
                authkey = tokenBarer
            ).execute()

            if (response.code() == 200) {
                response.body()
            } else
                throw ApiLoopException("get Otp error ${response.code()}")
        }.getValue("otp")
    }
}

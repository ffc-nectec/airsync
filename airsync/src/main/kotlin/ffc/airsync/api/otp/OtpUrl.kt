package ffc.airsync.api.otp

import ffc.airsync.APIVERSION
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface OtpUrl {
    @GET("/$APIVERSION/org/{orgId}/otp")
    fun get(
        @Header("Authorization") authkey: String,
        @Path("orgId") orgId: String
    ): Call<Map<String, String>>
}

package ffc.airsync.api.otp

import ffc.airsync.APIVERSION
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface OtpService {
    @GET("/$APIVERSION/org/{orgId}/otp")
    fun get(
        @Header("Authorization") authkey: String,
        @Path("orgId") orgId: String,
        @Query("random") random: String = UUID.randomUUID().toString()
    ): Call<Map<String, String>>
}

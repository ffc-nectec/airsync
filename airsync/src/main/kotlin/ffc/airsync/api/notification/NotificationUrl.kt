package ffc.airsync.api.notification

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.HashMap

interface NotificationUrl {
    @POST("/v0/org/{orgId}/firebasetoken")
    fun createFirebaseToken(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body firebaseToken: HashMap<String, String>
    ): Call<Void>
}

package ffc.airsync.api.notification

import ffc.airsync.APIVERSION
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.HashMap

interface NotificationService {
    @POST("/$APIVERSION/org/{orgId}/firebasetoken")
    fun createFirebaseToken(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body firebaseToken: HashMap<String, String>
    ): Call<Void>
}

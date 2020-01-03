package ffc.airsync.api.user

import ffc.airsync.APIVERSION
import ffc.entity.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @POST("/$APIVERSION/org/{orgId}/user")
    fun register(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body user: List<User>
    ): Call<List<User>>

    @GET("/$APIVERSION/org/{orgId}/user")
    fun get(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<List<User>>

    @PUT("/$APIVERSION/org/{orgId}/user/sync")
    fun sync(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body user: List<User>
    ): Call<List<User>>
}

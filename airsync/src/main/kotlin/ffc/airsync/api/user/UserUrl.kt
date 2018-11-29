package ffc.airsync.api.user

import ffc.entity.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface UserUrl {
    @POST("/v0/org/{orgId}/user")
    fun regisUser(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body user: List<User>
    ): Call<List<User>>
}

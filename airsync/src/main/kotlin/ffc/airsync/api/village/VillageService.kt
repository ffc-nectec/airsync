package ffc.airsync.api.village

import ffc.airsync.APIVERSION
import ffc.entity.Village
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface VillageService {

    @GET("/$APIVERSION/org/{orgId}/village")
    fun getHouse(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<List<Village>>

    @POST("/$APIVERSION/org/{orgId}/villages")
    fun create(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body villages: List<Village>
    ): Call<List<Village>>

    @POST("/$APIVERSION/org/{orgId}/village")
    fun createSingel(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body village: Village
    ): Call<Village>

    @PUT("/$APIVERSION/org/{orgId}/village/{villageId}")
    fun edit(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("villageId") villageId: String,
        @Body villages: Village
    ): Call<Village>

    @DELETE("/$APIVERSION/org/{orgId}/villages")
    fun deleteOrg(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Void>
}

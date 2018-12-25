package ffc.airsync.api.village

import ffc.entity.Village
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface VillageUrl {
    @POST("/v0/org/{orgId}/villages")
    fun create(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body villages: List<Village>
    ): Call<List<Village>>

    @POST("/v0/org/{orgId}/village")
    fun createSingel(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body village: Village
    ): Call<Village>

    @PUT("/v0/org/{orgId}/village/{villageId}")
    fun edit(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("villageId") villageId: String,
        @Body villages: Village
    ): Call<Village>

    @DELETE("/v0/org/{orgId}/villages")
    fun deleteOrg(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Void>
}

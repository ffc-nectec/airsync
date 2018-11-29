package ffc.airsync.api.healthcare

import ffc.entity.healthcare.HealthCareService
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HealthCareUrl {

    @POST("/v0/org/{orgId}/healthcareservice/sync/{block}")
    fun insertHealthCareBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int,
        @Body healthCare: List<@JvmSuppressWildcards HealthCareService>
    ): Call<List<HealthCareService>>

    @PUT("/v0/org/{orgId}/healthcareservice/sync/{block}")
    fun confirmHealthCareBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>

    @DELETE("/v0/org/{orgId}/healthcareservice/sync/{block}")
    fun unConfirmHealthCareBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>

    @GET("/v0/org/{orgId}/healthcareservice/{visitId}")
    fun getHomeVisit(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("visitId") id: String
    ): Call<HealthCareService>

    @POST("/v0/org/{orgId}/healthcareservices")
    fun createHomeVisit(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body homeVisit: List<HealthCareService>
    ): Call<List<HealthCareService>>

    @DELETE("/v0/org/{orgId}/healthcareservices")
    fun cleanHealthCare(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Void>
}

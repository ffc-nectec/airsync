package ffc.airsync.api.analyzer

import ffc.airsync.APIVERSION
import ffc.entity.healthcare.analyze.HealthAnalyzer
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AnalyzerUrl {

    @POST("/$APIVERSION/org/{orgId}/person/{personId}/healthanalyze")
    fun createHealthAnalyze(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("personId") personId: String,
        @Body healtyAnalyzer: HealthAnalyzer
    ): Call<HealthAnalyzer>

    @GET("/$APIVERSION/org/{orgId}/person/{personId}/healthanalyze")
    fun getHealthAnalyze(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("personId") personId: String
    ): Call<HealthAnalyzer>

    @DELETE("/$APIVERSION/org/{orgId}/person/{personId}/healthanalyze")
    fun removeHealthAnalyze(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("personId") personId: String
    ): Call<Void>

    @DELETE("/$APIVERSION/org/{orgId}/healthanalyze")
    fun cleanHealthAnalyzeOrgId(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Void>

    @POST("/$APIVERSION/org/{orgId}/healthanalyzes/sync/{block}")
    fun insertBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int,
        @Body healtyAnalyzer: Map<String, HealthAnalyzer>
    ): Call<Map<String, HealthAnalyzer>>

    @GET("/$APIVERSION/org/{orgId}/healthanalyzes/sync/{block}")
    fun getBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Map<String, HealthAnalyzer>>

    @PUT("/$APIVERSION/org/{orgId}/healthanalyzes/sync/{block}")
    fun confirmBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>

    @DELETE("/$APIVERSION/org/{orgId}/healthanalyzes/sync/{block}")
    fun unConfirmBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>
}

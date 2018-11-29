package ffc.airsync.api.analyzer

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

    @POST("/v0/org/{orgId}/person/{personId}/healthanalyze")
    fun createHealthAnalyze(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("personId") personId: String,
        @Body healtyAnalyzer: HealthAnalyzer
    ): Call<HealthAnalyzer>

    @GET("/v0/org/{orgId}/person/{personId}/healthanalyze")
    fun getHealthAnalyze(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("personId") personId: String
    ): Call<HealthAnalyzer>

    @DELETE("/v0/org/{orgId}/person/{personId}/healthanalyze")
    fun removeHealthAnalyze(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("personId") personId: String
    ): Call<Void>

    @DELETE("/v0/org/{orgId}/healthanalyze")
    fun cleanHealthAnalyzeOrgId(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Void>

    @POST("/v0/org/{orgId}/healthanalyze/sync/{block}")
    fun insertBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int,
        @Body healtyAnalyzer: Map<String, HealthAnalyzer>
    ): Call<Map<String, HealthAnalyzer>>

    @GET("/v0/org/{orgId}/healthanalyze/sync/{block}")
    fun getBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Map<String, HealthAnalyzer>>

    @PUT("/v0/org/{orgId}/healthanalyze/sync/{block}")
    fun confirmBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>

    @DELETE("/v0/org/{orgId}/healthanalyze/sync/{block}")
    fun unConfirmBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>
}

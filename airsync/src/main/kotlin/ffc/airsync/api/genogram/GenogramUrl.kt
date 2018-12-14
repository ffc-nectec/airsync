package ffc.airsync.api.genogram

import ffc.entity.Person
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GenogramUrl {
    @PUT("/v0/org/{orgId}/person/{personId}/relationship")
    fun updateRelationship(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("personId") personId: String,
        @Body relationship: List<Person.Relationship>
    ): Call<List<Person.Relationship>>

    @POST("/v0/org/{orgId}/person/relationships/sync/{block}")
    fun insertBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int,
        @Body relationship: Map<String, @JvmSuppressWildcards List<Person.Relationship>>
    ): Call<Map<String, List<Person.Relationship>>>

    @GET("/v0/org/{orgId}/person/relationships/sync/{block}")
    fun getBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Map<String, List<Person.Relationship>>>

    @PUT("/v0/org/{orgId}/person/relationships/sync/{block}")
    fun confirmBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>

    @DELETE("/v0/org/{orgId}/person/relationships/sync/{block}")
    fun unConfirmBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>

    @DELETE("/v0/org/{orgId}/person/relationships/sync/clean")
    fun cleanAll(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Void>
}

package ffc.airsync.api.person

import ffc.airsync.APIVERSION
import ffc.entity.Person
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PersonUrl {
    @DELETE("/$APIVERSION/org/{orgId}/persons")
    fun clearnPerson(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Void>

    @POST("/$APIVERSION/org/{orgId}/persons")
    fun createPerson(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body personList: List<Person>
    ): Call<List<Person>>

    @POST("/$APIVERSION/org/{orgId}/persons/sync/{block}")
    fun insertPersonBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int,
        @Body personList: List<Person>
    ): Call<List<Person>>

    @PUT("/$APIVERSION/org/{orgId}/persons/sync/{block}")
    fun confirmPersonBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>

    @DELETE("/$APIVERSION/org/{orgId}/persons/sync/{block}")
    fun unConfirmPersonBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>
}

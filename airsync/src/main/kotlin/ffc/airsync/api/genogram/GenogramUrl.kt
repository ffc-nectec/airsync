package ffc.airsync.api.genogram

import ffc.entity.Person
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
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
}

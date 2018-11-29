package ffc.airsync.api.icd10

import ffc.entity.healthcare.Disease
import ffc.entity.healthcare.Icd10
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface Icd10Url {
    @GET("/v0/disease")
    fun lookupDisease(
        @Header("Authorization") authkey: String,
        @Query("query") query: String
    ): Call<List<Disease>>

    @GET("/v0/disease/icd10/{id}")
    fun lookupIcd10(
        @Header("Authorization") authkey: String,
        @Path("id") id: String
    ): Call<Icd10>
}

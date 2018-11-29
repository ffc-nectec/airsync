package ffc.airsync.api.homehealthtype

import ffc.entity.healthcare.CommunityService
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface HomeHealtyTypeUrl {
    @GET("/v0/homehealth/{id}")
    fun lookupCommunityServiceType(
        @Header("Authorization") authkey: String,
        @Path("id") id: String
    ): Call<CommunityService.ServiceType>
}

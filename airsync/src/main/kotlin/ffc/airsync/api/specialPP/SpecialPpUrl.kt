package ffc.airsync.api.specialPP

import ffc.entity.healthcare.SpecialPP
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface SpecialPpUrl {
    @GET("/v0/specialPP/{id}")
    fun lookupSpecialPP(
        @Header("Authorization") authkey: String,
        @Path("id") id: String
    ): Call<SpecialPP.PPType>
}

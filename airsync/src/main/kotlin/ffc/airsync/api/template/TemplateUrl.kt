package ffc.airsync.api.template

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface TemplateUrl {
    @POST("/v0/org/{orgId}/template")
    fun lookupSpecialPP(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Void>
}

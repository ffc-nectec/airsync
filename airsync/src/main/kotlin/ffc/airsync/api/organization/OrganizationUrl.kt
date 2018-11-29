package ffc.airsync.api.organization

import ffc.entity.Organization
import ffc.entity.Token
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface OrganizationUrl {
    @POST("/v0/org")
    fun regisOrg(@Body body: Organization): Call<Organization>

    @POST("/v0/org/{orgId}/authorize")
    fun loginOrg(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Token>
}

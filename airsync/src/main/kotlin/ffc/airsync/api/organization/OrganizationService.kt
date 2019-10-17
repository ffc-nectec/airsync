package ffc.airsync.api.organization

import ffc.airsync.APIVERSION
import ffc.entity.Organization
import ffc.entity.Token
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface OrganizationService {
    @POST("/$APIVERSION/org")
    fun regisOrg(@Body body: Organization): Call<Organization>

    @POST("/$APIVERSION/org/{orgId}/authorize")
    fun loginOrg(
        @Path("orgId") orgId: String,
        @Body bodyLogin: Map<String, String>
    ): Call<Token>
}

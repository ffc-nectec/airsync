package ffc.airsync.api.template

import ffc.airsync.APIVERSION
import ffc.entity.Template
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface TemplateService {
    @POST("/$APIVERSION/org/{orgId}/template")
    fun clearnAndCreate(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body template: List<Template>
    ): Call<Void>
}

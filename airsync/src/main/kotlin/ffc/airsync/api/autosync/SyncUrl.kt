package ffc.airsync.api.autosync

import ffc.airsync.APIVERSION
import ffc.entity.Entity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface SyncUrl {
    @GET("/$APIVERSION/org/{orgId}/sync")
    fun syncData(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<List<Entity>>
}

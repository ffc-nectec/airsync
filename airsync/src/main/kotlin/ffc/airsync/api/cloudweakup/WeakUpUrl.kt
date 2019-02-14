package ffc.airsync.api.cloudweakup

import ffc.airsync.APIVERSION
import retrofit2.Call
import retrofit2.http.GET

interface WeakUpUrl {
    @GET("/$APIVERSION")
    fun checkCloud(): Call<Void>
}

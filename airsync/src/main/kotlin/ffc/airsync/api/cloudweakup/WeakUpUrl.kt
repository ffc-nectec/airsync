package ffc.airsync.api.cloudweakup

import retrofit2.Call
import retrofit2.http.GET

interface WeakUpUrl {
    @GET("/v0")
    fun checkCloud(): Call<Void>
}

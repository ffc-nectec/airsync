package ffc.airsync.api.house

import ffc.airsync.APIVERSION
import ffc.entity.place.House
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HouseUrl {
    @POST("/$APIVERSION/org/{orgId}/houses")
    fun createHouse(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body houseList: List<House>
    ): Call<List<House>>

    @GET("/$APIVERSION/org/{orgId}/house/{house_id}")
    fun getHouse(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("house_id") _id: String
    ): Call<House>

    @PUT("/$APIVERSION/org/{orgId}/house/{house_id}")
    fun putHouse(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("house_id") _id: String,
        @Body house: House
    ): Call<Void>

    @DELETE("/$APIVERSION/org/{orgId}/houses")
    fun clernHouse(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Void>

    @POST("/$APIVERSION/org/{orgId}/houses/sync/{block}")
    fun insertHouseBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int,
        @Body houseList: List<House>
    ): Call<List<House>>

    @PUT("/$APIVERSION/org/{orgId}/houses/sync/{block}")
    fun confirmHouseBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>

    @DELETE("/$APIVERSION/org/{orgId}/houses/sync/{block}")
    fun unConfirmHouseBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>
}

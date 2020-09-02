/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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

interface HouseService {
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

    @PUT("/$APIVERSION/org/{orgId}/houses")
    fun putHouses(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body houseList: List<House>
    ): Call<List<House>>

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

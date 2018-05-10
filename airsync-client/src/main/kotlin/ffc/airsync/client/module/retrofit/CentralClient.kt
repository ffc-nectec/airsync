/*
 * Copyright (c) 2561 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.airsync.client.module.retrofit

import ffc.model.*
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface CentralClient {
    @POST("/v0/org")
    fun regisOrg(@Body body: Organization): Call<Organization>


    @POST("/v0/org/{orgId}/user")
    fun regisUser(@Path("orgId") orgId: String, @Header("Authorization") authkey: String, @Body user: List<User>): Call<Void>


    @POST("/v0/org/{orgId}/place/house")
    fun createHouse(@Path("orgId") orgId: String, @Header("Authorization") authkey: String, @Body houseList: List<Address>): Call<Void>

    @POST("/v0/org/{orgId}/person")
    fun createPerson(@Path("orgId") orgId: String,
                     @Header("Authorization") authkey: String,
                     @Body personList: List<Person>): Call<Void>


    @POST("/v0/org/{orgId}/chronic/base")
    fun createChronic(@Path("orgId") orgId: String,
                      @Header("Authorization") authkey: String,
                      @Body chronicList: List<Chronic>): Call<Void>

    @GET("/v0/org/{orgId}/place/house/action")
    fun syncHouseAction(@Path("orgId") orgId: String,
                        @Header("Authorization") authkey: String): Call<List<ActionHouse>>


    @GET("/v0/org/{orgId}/place/house/{house_id}")
    fun getHouse(@Path("orgId") orgId: String,
                 @Header("Authorization") authkey: String,
                 @Path("house_id") _id: String): Call<Address>


    @PUT("/v0/org/{orgId}/place/house/{house_id}")
    fun putHouse(@Path("orgId") orgId: String,
                 @Header("Authorization") authkey: String,
                 @Path("house_id") _id: String,
                 @Body house: Address)
      : Call<Void>


    @PUT("/v0/org/{orgId}/place/house/action")
    fun putSyncUpdateStatus(@Path("orgId") orgId: String,
                            @Query("id") actionId: UUID,
                            @Query("status") status: ActionHouse.STATUS,
                            @Header("Authorization") authkey: String): Call<Void>

    @PUT("/v0/org/{orgId}/firebase")
    fun createFirebaseToken(@Path("orgId") orgId: String, @Header("Authorization") authkey: String, @Body firebaseToken: FirebaseToken): Call<Void>


}

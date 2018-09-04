/*
 * Copyright (c) 2018 NECTEC
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

package ffc.airsync.api.retrofit

import ffc.entity.House
import ffc.entity.Organization
import ffc.entity.Person
import ffc.entity.Token
import ffc.entity.User
import ffc.entity.healthcare.Chronic
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.HashMap

interface RetofitFunctionCallUrl {

    @POST("/v0/org")
    fun regisOrg(@Body body: Organization): Call<Organization>

    @POST("/v0/org/{orgId}/authorize")
    fun loginOrg(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Token>

    @POST("/v0/org/{orgId}/user")
    fun regisUser(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body user: List<User>
    ): Call<Void>

    @POST("/v0/org/{orgId}/place/houses")
    fun createHouse(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body houseList: List<House>
    ): Call<List<House>>

    @POST("/v0/org/{orgId}/person")
    fun createPerson(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body personList: List<Person>
    ): Call<Void>

    @POST("/v0/org/{orgId}/chronic/base")
    fun createChronic(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body chronicList: List<Chronic>
    ): Call<Void>

    @GET("/v0/org/{orgId}/place/house/{house_id}")
    fun getHouse(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("house_id") _id: String
    ): Call<House>

    @PUT("/v0/org/{orgId}/place/house/{house_id}")
    fun putHouse(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("house_id") _id: String,
        @Body house: House
    ): Call<Void>

    @POST("/v0/org/{orgId}/firebasetoken")
    fun createFirebaseToken(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body firebaseToken: HashMap<String, String>
    ): Call<Void>
}

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

package ffc.airsync.retrofit

import ffc.entity.Entity
import ffc.entity.Organization
import ffc.entity.Person
import ffc.entity.Token
import ffc.entity.User
import ffc.entity.healthcare.CommunityServiceType
import ffc.entity.healthcare.Disease
import ffc.entity.healthcare.HomeVisit
import ffc.entity.place.House
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.HashMap

interface RetofitRestUrl {
    @GET("/v0")
    fun checkCloud(): Call<Void>

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
    ): Call<List<User>>

    @POST("/v0/org/{orgId}/houses")
    fun createHouse(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body houseList: List<House>
    ): Call<List<House>>

    @DELETE("/v0/org/{orgId}/houses")
    fun clernHouse(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Void>

    @DELETE("/v0/org/{orgId}/persons")
    fun clearnPerson(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Void>

    @POST("/v0/org/{orgId}/persons")
    fun createPerson(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body personList: List<Person>
    ): Call<List<Person>>

    @PUT("/v0/org/{orgId}/person/{personId}/relationship")
    fun updateRelationship(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("personId") personId: String,
        @Body relationship: List<Person.Relationship>
    ): Call<List<Person.Relationship>>

    @GET("/v0/org/{orgId}/house/{house_id}")
    fun getHouse(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("house_id") _id: String
    ): Call<House>

    @PUT("/v0/org/{orgId}/house/{house_id}")
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

    @GET("/v0/org/{orgId}/healthcareservice/{visitId}")
    fun getHomeVisit(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("visitId") id: String
    ): Call<HomeVisit>

    @POST("/v0/org/{orgId}/healthcareservices")
    fun createHomeVisit(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body homeVisit: List<HomeVisit>
    ): Call<List<HomeVisit>>

    @DELETE("/v0/org/{orgId}/healthcareservices")
    fun deleteHomeVisit(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Void>

    @GET("/v0/homehealth")
    fun lookupCommunityServiceType(
        @Header("Authorization") authkey: String,
        @Query("query") query: String
    ): Call<List<CommunityServiceType>>

    @GET("/v0/disease")
    fun lookupDisease(
        @Header("Authorization") authkey: String,
        @Query("query") query: String
    ): Call<List<Disease>>

    @GET("/v0/org/{orgId}/sync")
    fun syncData(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<List<Entity>>
}

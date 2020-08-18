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

package ffc.airsync.api.genogram

import ffc.airsync.APIVERSION
import ffc.entity.Person
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GenogramService {
    @PUT("/$APIVERSION/org/{orgId}/person/{personId}/relationship")
    fun updateRelationship(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("personId") personId: String,
        @Body relationship: List<Person.Relationship>
    ): Call<List<Person.Relationship>>

    @POST("/$APIVERSION/org/{orgId}/person/relationships/sync/{block}")
    fun insertBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int,
        @Body relationship: Map<String, @JvmSuppressWildcards List<Person.Relationship>>
    ): Call<Map<String, List<Person.Relationship>>>

    @GET("/$APIVERSION/org/{orgId}/person/relationships/sync/{block}")
    fun getBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Map<String, List<Person.Relationship>>>

    @PUT("/$APIVERSION/org/{orgId}/person/relationships/sync/{block}")
    fun confirmBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>

    @DELETE("/$APIVERSION/org/{orgId}/person/relationships/sync/{block}")
    fun unConfirmBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>

    @DELETE("/$APIVERSION/org/{orgId}/person/relationships/sync/clean")
    fun cleanAll(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Void>
}

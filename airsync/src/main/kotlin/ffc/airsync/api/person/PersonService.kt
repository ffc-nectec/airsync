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

package ffc.airsync.api.person

import ffc.airsync.APIVERSION
import ffc.entity.Person
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

internal interface PersonService {
    @DELETE("/$APIVERSION/org/{orgId}/persons")
    fun clearnPerson(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Void>

    @POST("/$APIVERSION/org/{orgId}/persons")
    fun createPerson(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body personList: List<Person>
    ): Call<List<Person>>

    @POST("/$APIVERSION/org/{orgId}/persons/sync/{block}")
    fun createPersonBySyncProtocol(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int,
        @Body personList: List<Person>
    ): Call<List<Person>>

    @PUT("/$APIVERSION/org/{orgId}/persons/sync/{block}")
    fun confirmPersonSyncProtocolBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>

    @PUT("/$APIVERSION/org/{orgId}/persons")
    fun updatePersons(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body persons: List<Person>
    ): Call<List<Person>>

    @DELETE("/$APIVERSION/org/{orgId}/persons/sync/{block}")
    fun unConfirmPersonSyncProtocolBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>
}

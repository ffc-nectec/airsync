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

package ffc.airsync.api.healthcare

import ffc.airsync.APIVERSION
import ffc.entity.healthcare.HealthCareService
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HealthCareServiceUrl {

    @POST("/$APIVERSION/org/{orgId}/healthcareservices/sync/{block}")
    fun insertHealthCareBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int,
        @Body healthCare: List<@JvmSuppressWildcards HealthCareService>
    ): Call<List<HealthCareService>>

    @PUT("/$APIVERSION/org/{orgId}/healthcareservices/sync/{block}")
    fun confirmHealthCareBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>

    @DELETE("/$APIVERSION/org/{orgId}/healthcareservices/sync/{block}")
    fun unConfirmHealthCareBlock(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("block") block: Int
    ): Call<Void>

    @GET("/$APIVERSION/org/{orgId}/healthcareservice/{visitId}")
    fun getHomeVisit(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("visitId") id: String
    ): Call<HealthCareService>

    @PUT("/$APIVERSION/org/{orgId}/healthcareservice/{visitId}")
    fun updateHomeVisit(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Path("visitId") visitId: String,
        @Body homeVisit: HealthCareService
    ): Call<HealthCareService>

    @POST("/$APIVERSION/org/{orgId}/healthcareservices")
    fun createHomeVisit(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String,
        @Body homeVisit: List<HealthCareService>
    ): Call<List<HealthCareService>>

    @DELETE("/$APIVERSION/org/{orgId}/healthcareservices")
    fun cleanHealthCare(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Void>
}

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

package ffc.airsync.api.organization

import ffc.airsync.APIVERSION
import ffc.entity.Organization
import ffc.entity.Token
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface OrganizationService {
    @POST("/$APIVERSION/org")
    fun regisOrg(@Body body: Organization): Call<Organization>

    @POST("/$APIVERSION/org/{orgId}/authorize")
    fun loginOrg(
        @Path("orgId") orgId: String,
        @Body bodyLogin: Map<String, String>
    ): Call<Token>

    @DELETE("/$APIVERSION/org/{orgId}")
    fun removeOrganization(
        @Path("orgId") orgId: String,
        @Header("Authorization") authkey: String
    ): Call<Void>

    @DELETE("/$APIVERSION/org/{orgId}/{name}")
    fun removeOrganizationStampName(
        @Path("orgId") orgId: String,
        @Path("name") orgName: String
    ): Call<Void>
}

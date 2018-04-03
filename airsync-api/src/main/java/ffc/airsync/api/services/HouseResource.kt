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

package ffc.airsync.api.services

import ffc.airsync.api.services.module.HttpRestOrgService
import ffc.airsync.api.services.module.OrgService
import ffc.model.Address
import ffc.model.Organization
import ffc.model.User
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.xml.bind.DatatypeConverter

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/org")
class HouseResource {

    val orgServices: OrgService = HttpRestOrgService.instant


    @GET
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house")
    fun getHouse(@QueryParam("page") page: Int = 1,
                 @QueryParam("per_page") per_page: Int = 1,
                 @Context req: HttpServletRequest) {
        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")


    }

    @POST
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house/base")
    fun createPlace(@Context req: HttpServletRequest,
                    @PathParam("orgId") orgId: String,
                    houseList: List<Address>): Response {
        println("\nCall create house by ip = " + req.remoteAddr)

        houseList.forEach {
            println(it)
        }

        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")


        if (token != null) {
            orgServices.createHouse(token, orgId, houseList)
            return Response.status(Response.Status.CREATED).build()
        } else {
            throw NotAuthorizedException("Not Pass")
        }
    }
}

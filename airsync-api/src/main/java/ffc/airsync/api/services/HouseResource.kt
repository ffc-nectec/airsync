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

import ffc.airsync.api.services.module.HouseService
import ffc.airsync.api.services.module.HttpRestOrgService
import ffc.airsync.api.services.module.OrgService
import ffc.model.Address
import ffc.model.printDebug
import me.piruin.geok.geometry.FeatureCollection
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/org")
class HouseResource {

    val orgServices: OrgService = HttpRestOrgService.instant


    @Produces("application/vnd.geo+json")
    @Consumes("application/vnd.geo+json")
    @GET
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house")
    fun getHouse(@QueryParam("page") page: Int = 1,
                 @QueryParam("per_page") per_page: Int = 200,
                 @QueryParam("hid") hid: Int = -1,
                 @PathParam("orgId") orgId: String,
                 @Context req: HttpServletRequest): FeatureCollection {
        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")



        printDebug("getHouse method geoJson List paramete orgId $orgId page $page per_page $per_page hid $hid")


        val geoJso: FeatureCollection = HouseService.get(token, orgId, if (page == 0) 1 else page, if (per_page == 0) 200 else per_page, if (hid == 0) -1 else hid)

        geoJso.features.forEach {
            printDebug(it.geometry)
        }

        return geoJso
    }


    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house/{houseId:([\\dabcdefABCDEF]{6})}")
    fun putHouse(@Context req: HttpServletRequest,
                 @PathParam("orgId") orgId: String,
                 @PathParam("houseId") houseId: String
                 , house: Address
    ): Response {
        printDebug("\nCall create house by ip = " + req.remoteAddr + " OrgID $orgId")

        //printDebug(dd)
        printDebug("hid ${house.hid} id ${house.id} latLng ${house.coordinates}")

        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")

        if (house.coordinates == null) throw javax.ws.rs.NotSupportedException("coordinates null")
        HouseService.update(token, orgId, house, houseId)

        return Response.status(200).build()

    }


    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house")
    fun createPlace(@Context req: HttpServletRequest,
                    @PathParam("orgId") orgId: String,
                    houseList: List<Address>): Response {
        printDebug("\nCall create house by ip = " + req.remoteAddr)

        houseList.forEach {
            printDebug(it)
        }

        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")
        HouseService.create(token, orgId, houseList)
        return Response.status(Response.Status.CREATED).build()

    }
}

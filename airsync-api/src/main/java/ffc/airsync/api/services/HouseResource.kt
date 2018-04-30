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
import ffc.model.ActionHouse
import ffc.model.Address
import ffc.model.printDebug
import ffc.model.toJson
import me.piruin.geok.geometry.FeatureCollection
import java.util.*
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
                 @Context req: HttpServletRequest): FeatureCollection<Address> {
        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")



        printDebug("getHouse method geoJson List paramete orgId $orgId page $page per_page $per_page hid $hid")


        val geoJso = HouseService.get(token, orgId, if (page == 0) 1 else page, if (per_page == 0) 200 else per_page, if (hid == 0) -1 else hid)

        printDebug("Print feture before return to rest")
        geoJso.features.forEach {
            printDebug(it.geometry)
        }

        return geoJso
    }


    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house/{houseId:([\\dabcdefABCDEF]{24})}")
    fun putHouse(@Context req: HttpServletRequest,
                 @PathParam("orgId") orgId: String,
                 @PathParam("houseId") houseId: String
                 , house: Address
    ): Response {
        printDebug("Call create house by ip = " + req.remoteAddr + " OrgID $orgId")

        //printDebug(dd)
        printDebug("hid ${house.hid} _id ${house._id} latLng ${house.coordinates}")

        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")

        if (house.coordinates == null) throw javax.ws.rs.NotSupportedException("coordinates null")
        HouseService.update(token, orgId, house, houseId)

        return Response.status(200).build()

    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house/{houseId:([\\dabcdefABCDEF]{24})}")
    fun getSingleHouse(@Context req: HttpServletRequest,
                       @PathParam("orgId") orgId: String,
                       @PathParam("houseId") houseId: String
    ): Address {
        printDebug("Call get single house by ip = " + req.remoteAddr + " OrgID $orgId House ID = $houseId")


        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")


        val house: Address = HouseService.getSingleHouse(token, orgId, houseId)

        return house

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
            it.people = null
            it.haveChronics = null
            printDebug("house json = " + it.toJson())
        }

        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")
        HouseService.create(token, orgId, houseList)
        return Response.status(Response.Status.CREATED).build()

    }


    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house/action")
    fun getHouseAction(@QueryParam("page") page: Int = 1,
                       @QueryParam("per_page") per_page: Int = 200,
                       @PathParam("orgId") orgId: String,
                       @Context req: HttpServletRequest): List<ActionHouse> {
        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")



        printDebug("getHouse method getHouseAction paramete orgId $orgId page $page per_page $per_page")


        val actionList = HouseService.getAction(token = token,
          orgId = orgId)
        if (actionList.isEmpty()) throw NotFoundException("ไม่มี Action List")

        return actionList
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house/action")
    fun getUpdateCompleateAction(
      @PathParam("orgId") orgId: String,
      @QueryParam("id") actionId: UUID,
      @QueryParam("status") status: ActionHouse.STATUS = ActionHouse.STATUS.COMPLETE,
      @Context req: HttpServletRequest): Response {
        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")



        printDebug("put update action paramete orgId $orgId actionId $actionId status $status")


        HouseService.updateActionComplete(token = token,
          orgId = orgId,
          actionId = actionId)

        return Response.status(200).build()

    }

}


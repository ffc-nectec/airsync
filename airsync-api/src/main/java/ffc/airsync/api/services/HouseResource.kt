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

import ffc.airsync.api.services.filter.FfcSecurityContext
import ffc.airsync.api.services.module.HouseService
import ffc.model.Address
import ffc.model.TokenMessage
import ffc.model.printDebug
import ffc.model.toJson
import me.piruin.geok.geometry.FeatureCollection
import java.util.*
import javax.annotation.security.RolesAllowed
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

@Path("/org")
class HouseResource {

    @Context
    private var context: SecurityContext? = null

    @RolesAllowed("USER", "ORG")
    @Produces(GEOJSONHeader)
    @GET
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house")
    fun getGeoJsonHouse(@QueryParam("page") page: Int = 1,
                        @QueryParam("per_page") per_page: Int = 200,
                        @QueryParam("hid") hid: Int = -1,
                        @PathParam("orgId") orgId: String,
                        @Context req: HttpServletRequest): FeatureCollection<Address> {
        val httpHeader = req.buildHeaderMap()

        printDebug("getGeoJsonHouse house method geoJson List paramete orgId $orgId page $page per_page $per_page hid $hid")


        val geoJso = HouseService.getGeoJsonHouse(
          orgId,
          if (page == 0) 1 else page,
          if (per_page == 0) 200 else per_page,
          if (hid == 0) -1 else hid)

        printDebug("Print feture before return to rest")
        geoJso.features.forEach {
            printDebug(it.geometry)
        }

        return geoJso
    }


    @RolesAllowed("USER", "ORG")
    @GET
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house")
    fun getJsonHouse(@QueryParam("page") page: Int = 1,
                     @QueryParam("per_page") per_page: Int = 200,
                     @QueryParam("hid") hid: Int = -1,
                     @PathParam("orgId") orgId: String,
                     @Context req: HttpServletRequest): List<Address> {
        val httpHeader = req.buildHeaderMap()

        printDebug("getGeoJsonHouse house method geoJson List paramete orgId $orgId page $page per_page $per_page hid $hid")


        val jsonHouse = HouseService.getJsonHouse(
          orgId,
          if (page == 0) 1 else page,
          if (per_page == 0) 200 else per_page,
          if (hid == 0) -1 else hid)

        printDebug("Print feture before return to rest")


        return jsonHouse
    }


    @RolesAllowed("USER", "ORG")
    @PUT
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house/{houseId:([\\dabcdefABCDEF]{24})}")
    fun update(@Context req: HttpServletRequest,
               @PathParam("orgId") orgId: String,
               @PathParam("houseId") houseId: String
               , house: Address
    ): Response {
        printDebug("Call put house by ip = " + req.remoteAddr + " OrgID $orgId")

        printDebug("\thid ${house.hid} _id ${house._id} latLng ${house.coordinates}")

        if (context == null) {
            printDebug("\tContext is null")
        }
        val role = getTokenRole(context!!)
        printDebug("\tRole $role")

        printDebug("\t${context!!.userPrincipal}")

        if (house.coordinates == null) throw javax.ws.rs.NotSupportedException("coordinates null")


        HouseService.update(role, orgId, house, houseId)

        return Response.status(200).build()

    }


    @RolesAllowed("USER", "ORG")
    @Produces(GEOJSONHeader)
    @Consumes(GEOJSONHeader)
    @GET
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house/{houseId:([\\dabcdefABCDEF]{24})}")
    fun getSingleGeo(@Context req: HttpServletRequest,
                     @PathParam("orgId") orgId: String,
                     @PathParam("houseId") houseId: String
    ): FeatureCollection<Address> {
        printDebug("Call getGeoJsonHouse single geo json house by ip = " + req.remoteAddr + " OrgID $orgId House ID = $houseId")


        val httpHeader = req.buildHeaderMap()

        val house: FeatureCollection<Address> = HouseService.getSingleGeo(orgId, houseId)

        return house

    }


    @RolesAllowed("USER", "ORG")
    @GET
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house/{houseId:([\\dabcdefABCDEF]{24})}")
    fun getSingle(@Context req: HttpServletRequest,
                  @PathParam("orgId") orgId: String,
                  @PathParam("houseId") houseId: String
    ): Address {
        printDebug("Call getGeoJsonHouse single house by ip = " + req.remoteAddr + " OrgID $orgId House ID = $houseId")


        val httpHeader = req.buildHeaderMap()


        val house: Address = HouseService.getSingle(orgId, houseId)

        return house

    }


    @RolesAllowed("ORG")
    @POST
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/houses")
    fun create(@Context req: HttpServletRequest,
               @PathParam("orgId") orgId: String,
               houseList: List<Address>?): Response {
        printDebug("\nCall create house by ip = " + req.remoteAddr)
        if (houseList == null) throw BadRequestException()

        houseList.forEach {
            it.people = null
            it.haveChronics = null
            printDebug("house json = " + it.toJson())
        }

        val httpHeader = req.buildHeaderMap()
        HouseService.create(orgId, houseList)
        return Response.status(Response.Status.CREATED).build()

    }

    @RolesAllowed("ORG")
    @POST
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house")
    fun createSingle(@Context req: HttpServletRequest,
                     @PathParam("orgId") orgId: String,
                     house: Address?): Response {
        printDebug("\nCall create house by ip = " + req.remoteAddr)
        if (house == null) throw BadRequestException()
        house.people = null
        house.haveChronics = null
        printDebug("house json = " + house.toJson())


        val httpHeader = req.buildHeaderMap()
        HouseService.create(orgId, house)
        return Response.status(Response.Status.CREATED).build()
    }

}

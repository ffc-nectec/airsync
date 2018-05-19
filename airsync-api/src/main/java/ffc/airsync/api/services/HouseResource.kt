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
import ffc.model.Address
import ffc.model.printDebug
import ffc.model.toJson
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

    @Produces("application/vnd.geo+json")
    @Consumes("application/vnd.geo+json")
    @GET
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house")
    fun get(@QueryParam("page") page: Int = 1,
            @QueryParam("per_page") per_page: Int = 200,
            @QueryParam("hid") hid: Int = -1,
            @PathParam("orgId") orgId: String,
            @Context req: HttpServletRequest): FeatureCollection<Address> {
        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("")


        printDebug("get house method geoJson List paramete orgId $orgId page $page per_page $per_page hid $hid")


        val geoJso = HouseService.get(
          token,
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


    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house/{houseId:([\\dabcdefABCDEF]{24})}")
    fun update(@Context req: HttpServletRequest,
               @PathParam("orgId") orgId: String,
               @PathParam("houseId") houseId: String
               , house: Address
    ): Response {
        printDebug("Call put house by ip = " + req.remoteAddr + " OrgID $orgId")

        //printDebug(dd)
        printDebug("hid ${house.hid} _id ${house._id} latLng ${house.coordinates}")

        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")

        if (house.coordinates == null) throw javax.ws.rs.NotSupportedException("coordinates null")
        HouseService.update(token, orgId, house, houseId)

        return Response.status(200).build()

    }

    @Produces("application/vnd.geo+json")
    @Consumes("application/vnd.geo+json")
    @GET
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house/{houseId:([\\dabcdefABCDEF]{24})}")
    fun getSingleGeo(@Context req: HttpServletRequest,
                     @PathParam("orgId") orgId: String,
                     @PathParam("houseId") houseId: String
    ): FeatureCollection<Address> {
        printDebug("Call get single geo json house by ip = " + req.remoteAddr + " OrgID $orgId House ID = $houseId")


        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")


        val house: FeatureCollection<Address> = HouseService.getSingleGeo(token, orgId, houseId)

        return house

    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house/{houseId:([\\dabcdefABCDEF]{24})}")
    fun getSingle(@Context req: HttpServletRequest,
                  @PathParam("orgId") orgId: String,
                  @PathParam("houseId") houseId: String
    ): Address {
        printDebug("Call get single house by ip = " + req.remoteAddr + " OrgID $orgId House ID = $houseId")


        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")


        val house: Address = HouseService.getSingle(token, orgId, houseId)

        return house

    }


    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
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
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")
        HouseService.create(token, orgId, houseList)
        return Response.status(Response.Status.CREATED).build()

    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
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
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")
        HouseService.create(token, orgId, house)
        return Response.status(Response.Status.CREATED).build()

    }

}


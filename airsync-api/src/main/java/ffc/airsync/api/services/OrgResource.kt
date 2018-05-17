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


import ffc.airsync.api.services.module.OrgService
import ffc.model.*
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/org")
class OrgResource {


    //Register orgUuid.
    @POST
    fun create(@Context req: HttpServletRequest, organization: Organization): Response {
        printDebug("Org register pcuCode = " + organization.pcuCode
          + " Name = " + organization.name
          + " UUID = " + organization.uuid)


        printDebug("Create my org")
        var ipAddress = req.getHeader("X-Forwarded-For")
        printDebug("\tGet ip address from header X-Forwarded-For = $ipAddress")
        printDebug("\tGet from req.remoteAddr = ${req.remoteAddr}")
        if (ipAddress == null) {
            ipAddress = req.remoteAddr
        }


        val orgUpdate = OrgService.register(organization, ipAddress)
        printDebug("\tGen ip = " + orgUpdate.lastKnownIp
          + " Org token = " + orgUpdate.token)

        return Response.status(Response.Status.CREATED).entity(orgUpdate).build()
    }

    @GET
    fun getMy(@QueryParam("my") my: Boolean = false,
              @Context req: HttpServletRequest): List<Organization> {
        printDebug("Get org my")
        var ipAddress = req.getHeader("X-Forwarded-For")
        printDebug("\tGet ip address from header X-Forwarded-For = $ipAddress")
        printDebug("\tGet from req.remoteAddr = ${req.remoteAddr}")
        if (ipAddress == null) {
            ipAddress = req.remoteAddr
        }

        printDebug("\tResult Org by ip = $ipAddress + my = $my")

        if (my) {
            return OrgService.getMy(ipAddress)
        } else {
            return OrgService.get()
        }
    }


    @DELETE
    @Path("/{orgId:([\\dabcdefABCDEF]+)}")
    fun remove(@PathParam("orgId") orgId: String,
               @Context req: HttpServletRequest): Response {

        printDebug("Remove org $orgId")
        val httpHeader = req.buildHeaderMap()
        printDebug("getHeader $httpHeader")
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")


        printDebug("Call removeOrg Service _id = $orgId token = $token")
        OrgService.remove(token, orgId)
        return Response.status(200).build()
    }

    //Post username to central.


    @GET
    @Path("/orgtemp")
    fun getOrgTemp(): Organization {
        return Organization(
          UUID.randomUUID(), "2",
          "Temp00xx33",
          "Template",
          "lldsiie883289oid",
          "ksaljhdfkjhskjhfu",
          "182.123.21.21",
          "lkjsafjlkjsaf")
    }


}
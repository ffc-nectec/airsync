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
import ffc.airsync.api.services.module.OrgServiceHttpRestService
import ffc.model.Organization
import ffc.model.TokenMessage
import ffc.model.User
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.xml.bind.DatatypeConverter

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/org")
class OrgAutorizeResource {


    val orgServices: OrgService = OrgServiceHttpRestService()


    //Register orgUuid.
    @POST
    fun create(@Context req: HttpServletRequest, organization: Organization): Organization {
        println("Org register pcuCode = " + organization.pcuCode
          + " Name = " + organization.name
          + " UUID = " + organization.uuid)
        val orgUpdate = orgServices.register(organization, req.remoteAddr)
        println("Gen ip = " + orgUpdate.lastKnownIp
          + " Org token = " + orgUpdate.orgToken)
        return orgUpdate
    }

    @GET
    fun getMyOrg(@QueryParam("my") my: Boolean = false,
                 @Context req: HttpServletRequest): List<Organization> {
        if (my) {
            return orgServices.getMyOrg(req.remoteAddr)
        }
        throw NotFoundException()
    }

    //Post user to central.
    @POST
    @Path("/{orgUuid:([\\dabcdefABCDEF].*)}/user")
    fun createUser(@Context req: HttpServletRequest,
                   @PathParam("orgUuid") orgId: String,
                   userList: ArrayList<User>) {
        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")


        if (token != null)
            orgServices.createUser(token, orgId, userList)


    }


    @POST
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/autorize")
    fun regisMobile(@Context req: HttpServletRequest,
                    @PathParam("orgId") orgId: String): TokenMessage {

        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Basic ", "")
        val userpass = DatatypeConverter.parseBase64Binary(token).toString(charset("UTF-8")).split(":")
        val user = userpass.get(index = 0)
        val pass = userpass.get(index = 1)
        println("Mobile Login Auid = " + orgId +
        " User = "+user+
        " Pass = "+pass)
        val tokenMessage = orgServices.orgUserAuth(orgId,user,pass)




        Thread.sleep(3000)


        return tokenMessage

    }


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

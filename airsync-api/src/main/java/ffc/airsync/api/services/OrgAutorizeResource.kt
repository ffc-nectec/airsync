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
import ffc.model.*
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.xml.bind.DatatypeConverter
import kotlin.collections.ArrayList

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/org")
class OrgAutorizeResource {


    val orgServices: OrgService = OrgServiceHttpRestService()


    //Register orgUuid.
    @POST
    fun create(@Context req: HttpServletRequest, organization: Organization): Response {
        println("Org register pcuCode = " + organization.pcuCode
          + " Name = " + organization.name
          + " UUID = " + organization.uuid)


        val orgUpdate = orgServices.register(organization, req.remoteAddr)
        println("Gen ip = " + orgUpdate.lastKnownIp
          + " Org token = " + orgUpdate.token)

        return Response.status(Response.Status.CREATED).entity(orgUpdate).build()
    }

    @GET
    fun getMyOrg(@QueryParam("my") my: Boolean = false,
                 @Context req: HttpServletRequest): List<Organization> {

        println("Get Org by ip = "+ req.remoteAddr)

        if (my) {
            return orgServices.getMyOrg(req.remoteAddr)
        }else{
            return orgServices.getOrg()
        }
    }

    //Post username to central.
    @POST
    @Path("/{orgUuid:([\\dabcdefABCDEF].*)}/user")
    fun createUser(@Context req: HttpServletRequest,
                   @PathParam("orgUuid") orgId: String,
                   userList: ArrayList<User>) :Response {
        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")

        println("Raw user list.")
        userList.forEach {
            println("User = "+it.username+" Pass = "+it.password)
        }

        if (token != null) {
            orgServices.createUser(token, orgId, userList)
            return Response.status(Response.Status.CREATED).build()
        }else{
            throw NotAuthorizedException("Not Pass")
        }



    }


    @POST
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/authorize")
    fun regisMobile(@Context req: HttpServletRequest,
                    @PathParam("orgId") orgId: String): Response {

        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Basic ", "")
        val userpass = DatatypeConverter.parseBase64Binary(token).toString(charset("UTF-8")).split(":")
        val user = userpass.get(index = 0)
        val pass = userpass.get(index = 1)
        println("Mobile Login Auid = " + orgId +
        " User = "+user+
        " Pass = "+pass)
        val tokenMessage = orgServices.orgUserAuth(orgId,user,pass)


        //Thread.sleep(3000)


        return Response.status(Response.Status.CREATED).entity(tokenMessage).build()

    }



    @GET
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/person")
    fun getPerson(@QueryParam("page") page: Int = 1,@QueryParam("per_page") per_page: Int = 1,@Context req: HttpServletRequest) {
        val httpHeader = req.buildHeaderMap()


    }


    @POST
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/place/house/base")
    fun createPlace(@Context req: HttpServletRequest,
                    @PathParam("orgId") orgId: String,
                    houseList : List<HouseOrg>) :Response {
        println("\nCall create house by ip = "+req.remoteAddr)

        houseList.forEach {
            println(it)
        }

        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")


        if (token != null) {
            orgServices.createHouse(token,orgId,houseList)
            return Response.status(Response.Status.CREATED).build()
        }else{
            throw NotAuthorizedException("Not Pass")
        }
    }


    @POST
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/person/base")
    fun createPerson(@Context req: HttpServletRequest,
                    @PathParam("orgId") orgId: String,
                    personList : List<PersonOrg>) :Response {
        println("\nCall create house by ip = "+req.remoteAddr)

        personList.forEach {
            println(it)
        }

        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")


        if (token != null) {
            orgServices.createPerson(token,orgId,personList)
            return Response.status(Response.Status.CREATED).build()
        }else{
            throw NotAuthorizedException("Not Pass")
        }
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

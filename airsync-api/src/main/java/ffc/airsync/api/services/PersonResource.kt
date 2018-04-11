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

import ffc.airsync.api.dao.DaoFactory
import ffc.airsync.api.services.module.HttpRestOrgService
import ffc.airsync.api.services.module.OrgService
import ffc.model.Person
import ffc.model.printDebug
import java.util.*
import javax.security.sasl.AuthenticationException
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import kotlin.collections.ArrayList

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/org")
class PersonResource {

    val orgServices: OrgService = HttpRestOrgService.instant

    @GET
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/person")
    fun getPerson(@QueryParam("page") page: Int = 1,
                  @QueryParam("per_page") per_page: Int = 1,
                  @PathParam("orgId") orgId: String,
                  @Context req: HttpServletRequest): Response {
        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")

        try {
            val personList = orgServices.getPerson(token!!, orgId)
            return Response.status(Response.Status.OK).entity(personList).build()
        } catch (ex: NotAuthorizedException) {
            return Response.status(401).build()
        }
    }

    @POST
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/person/base")
    fun createPerson(@Context req: HttpServletRequest,
                     @PathParam("orgId") orgId: String,
                     personList: List<Person>): Response {
        printDebug("\nCall create person by ip = " + req.remoteAddr)

        personList.forEach {
            printDebug(it)
        }

        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")


        printDebug("Check token")
        if (token != null) {
            orgServices.createPerson(token, orgId, personList)
            return Response.status(Response.Status.CREATED).build()
        } else {
            printDebug("Authun not pass")
            throw AuthenticationException("Not Pass")
        }
    }

    @GET
    @Path("/person/t")
    fun getPersonTemplate(): List<Person> {
        val personDao = DaoFactory().buildPersonDao()
        val personLit = personDao.find(UUID.fromString("00000000-0000-0000-0000-000000000010"))
        val listReturn: ArrayList<Person> = arrayListOf()

        personLit.forEach {
            listReturn.add(it.data)
        }


        return listReturn

    }
}

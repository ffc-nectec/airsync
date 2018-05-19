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
import ffc.airsync.api.services.module.PersonService
import ffc.model.Person
import ffc.model.printDebug
import java.util.*
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


    @GET
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/person")
    fun get(@QueryParam("page") page: Int = 1,
            @QueryParam("per_page") per_page: Int = 200,
            @PathParam("orgId") orgId: String,
            @Context req: HttpServletRequest): Response {
        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")

        try {
            val personList = PersonService.get(
              token,
              orgId,
              if (page == 0) 1 else page,
              if (per_page == 0) 200 else per_page)

            return Response.status(Response.Status.OK).entity(personList).build()
        } catch (ex: NotAuthorizedException) {
            return Response.status(401).build()
        }
    }


    @POST
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/person")
    fun create(@Context req: HttpServletRequest,
               @PathParam("orgId") orgId: String,
               personList: List<Person>): Response {
        printDebug("\nCall create person by ip = " + req.remoteAddr)

        personList.forEach {
            printDebug(it)
        }

        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")


        PersonService.create(token, orgId, personList)
        return Response.status(Response.Status.CREATED).build()

    }


    @GET
    @Path("/person/t")
    fun getTemplate(): List<Person> {
        val personDao = DaoFactory().buildPersonDao()
        val personLit = personDao.find(UUID.fromString("00000000-0000-0000-0000-000000000010"))
        val listReturn: ArrayList<Person> = arrayListOf()

        personLit.forEach {
            listReturn.add(it.data)
        }


        return listReturn
    }
}

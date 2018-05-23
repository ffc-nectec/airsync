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

import ffc.model.Chronic
import ffc.model.printDebug
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/org")
class ChronicResoruce {

    /*
    @POST
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/chronic/base")
    fun create(@Context req: HttpServletRequest,
               @PathParam("orgId") orgId: String,
               chronicList: List<Chronic>): Response {
        printDebug("\nCall create chronic by ip = " + req.remoteAddr)

        chronicList.forEach {
            printDebug(it)
        }

        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")

        ChronicService.create(token, orgId, chronicList)
        return Response.status(Response.Status.CREATED).build()

    }
    */


}

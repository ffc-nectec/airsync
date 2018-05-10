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

package ffc.airsync.client.webservice.webresources


import ffc.model.FirebaseMessage
import ffc.model.FirebaseToken
import ffc.model.fromJson
import ffc.model.printDebug
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/")
class FirebaseResource {

    @POST
    @Path("/token")
    fun updateFirebaseToken(@Context req: HttpServletRequest,
                            token: String): Response {
        printDebug("\nCall firebase update token by ip = " + req.remoteAddr)
        printDebug("Firebase token $token")
        val firebaseToken: FirebaseToken = token.fromJson()
        printDebug("Firebase token= $firebaseToken")


        val fbm = ffc.airsync.client.webservice.module.FirebaseMessage.instant

        printDebug("\tCall update Token")
        fbm.updateToken(firebaseToken)

        printDebug("\tBefore put Firebase token")

        return Response.status(Response.Status.CREATED).build()

    }


    @POST
    @Path("/event")
    fun updateFirebaseEvent(@Context req: HttpServletRequest,
                            message: String): Response {
        printDebug("\nCall firebase update token by ip = " + req.remoteAddr)
        printDebug("\tFirebase message data $message")
        val event: FirebaseMessage = message.fromJson()
        printDebug("\t\tID = ${event.message.data._id} Type = ${event.message.data.type} Url = ${event.message.data.url}")
        val data = event.message.data
        val fbm = ffc.airsync.client.webservice.module.FirebaseMessage.instant



        if (data.type == FirebaseMessage.Type.House)
            fbm.updateHouse(data)


        return Response.status(Response.Status.CREATED).build()

    }
}

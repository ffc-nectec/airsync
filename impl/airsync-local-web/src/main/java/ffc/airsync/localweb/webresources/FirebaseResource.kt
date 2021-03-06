/*
 * Copyright (c) 2018 NECTEC
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

import ffc.airsync.client.webservice.module.FirebaseMessage
import ffc.airsync.localweb.getLogger
import ffc.entity.Messaging
import ffc.entity.gson.parseTo
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/")
class FirebaseResource {

    private val logger by lazy { getLogger(this) }
    @POST
    @Path("/token")
    fun updateFirebaseToken(@Context req: HttpServletRequest, token: String): Response {
        logger.info("\nCall firebase update token by ip = " + req.remoteAddr)
        logger.debug("Firebase token $token")
        val firebaseToken = token.parseTo<HashMap<String, String>>()
        logger.trace("Firebase token = $firebaseToken")

        val fbm = FirebaseMessage.instant

        logger.trace("\tCall update Token")
        fbm.updateToken(firebaseToken)
        return Response.status(Response.Status.CREATED).build()
    }

    @POST
    @Path("/event")
    fun updateFirebaseEvent(@Context req: HttpServletRequest, message: String): Response {
        logger.info("\nCall firebase update token by ip = " + req.remoteAddr)
        logger.debug("\tFirebase message data $message")
        val event = message.parseTo<Payload>()
        logger.debug(
            "\t\t" +
                    "Type = ${event.message.data.type} " +
                    "Url = ${event.message.data.url}"
        )
        val data = event.message.data

        logger.trace("\tGet data message")
        val fbm = FirebaseMessage.instant

        logger.trace("\t Check house type.")
        try {
            if (data.type.isNotEmpty())
                fbm.update(data)
            else
                logger.error("\t\tNot type process in firebase")
        } catch (ex: Exception) {
            logger.error(ex, ex)
            throw ex
        }
        logger.trace("End update firebase event.")
        return Response.status(Response.Status.CREATED).build()
    }
}

data class Payload(val message: Message) {

    data class Message(val data: Messaging)
}

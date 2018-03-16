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

import ffc.airsync.api.services.module.PcuService
import ffc.airsync.api.services.module.PcuServiceHttpRestService
import ffc.model.Message
import ffc.model.MobileUserAuth
import ffc.model.Pcu
import ffc.model.toJson
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/pcu/")
class PcuResource {
    val pcuServices: PcuService = PcuServiceHttpRestService()

    @PUT
    fun put(@Context req: HttpServletRequest, pcu: Pcu): Pcu {
        val pcuUpdate = pcuServices.register(pcu, req.getRemoteAddr())
        return pcuUpdate
    }

    @POST
    fun post(@Context req: HttpServletRequest, message: Message<Any>): Response {

        println("Http Post /pcu data = " + message.toJson())
        if (message.action == Message.Action.GETUSER) {
            val pcu: Pcu = message.data as Pcu

            val userList: List<MobileUserAuth> = pcuServices.getMobileUser(pcu)
            return Response.status(Response.Status.OK).entity(userList).build()
        } else if (message.action == Message.Action.CONFIRMUSER) {
            val userAuth: MobileUserAuth = message.data as MobileUserAuth
            if (userAuth.checkUser == MobileUserAuth.UserStatus.PASS) {
                //When validate pass
                //sdf
                pcuServices.setUserPass(userAuth)
                println("User auth pass receive. User= " + userAuth.username)
                //println(userAuth.checkUser)

            }else if (userAuth.checkUser== MobileUserAuth.UserStatus.NOTPASS){
                println("User auth not pass receive. user= "+userAuth.username)
                pcuServices.setUserNotPass(userAuth)
            }
        }

        return Response.status(Response.Status.OK).build()
    }

    @GET
    @Path("/tem")
    fun gettemplate(): Pcu {
        val pcu = Pcu(UUID.randomUUID(), "225", "template", "safsafsdf", "sdfsdfa", "192.231.4.21", "sdkjfslkjfsa")
        return pcu
    }
}

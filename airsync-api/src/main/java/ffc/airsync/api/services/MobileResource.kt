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
import ffc.airsync.api.services.module.MobileHttpRestServiceV2
import ffc.airsync.api.services.module.MobileServices
import ffc.model.Message
import ffc.model.MobileUserAuth
import ffc.model.Pcu
import ffc.model.fromJson
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/mobile")

class MobileResource {
    companion object {
        val mobileHttpRestService = MobileHttpRestServiceV2()
    }

    private val pcuDao = DaoFactory().buildPcuDao()

    @GET
    fun find(@Context req: HttpServletRequest,
             @DefaultValue("false") @QueryParam("mypcu") mypcu: Boolean = false): List<Pcu> {

        val pcuList = arrayListOf<Pcu>()
        val pcuReturn = arrayListOf<Pcu>()
        if (mypcu) {
            pcuList.add(pcuDao.findByIpAddress(req.getRemoteAddr()))
        } else {
            pcuList.addAll(pcuDao.find())

        }

        if (pcuList.isEmpty()) {
            throw NotFoundException("Not pcu")
        }

        pcuList.forEach {
            val pcu = Pcu(it.uuid,it.code,it.name)
            pcuReturn.add(pcu)
        }

        return pcuReturn
    }

    @POST
    fun post(@Context req: HttpServletRequest, message: Message): Response {
        var messageReceive = messagetemplate
        println("Post pcu action = "+ message.action)
        mobileHttpRestService.sendAndRecive(message, object : MobileServices.OnReceiveListener {
            override fun onReceive(message: String) {
                println("Http POST pcu")
                messageReceive  = message.fromJson()
            }

        })
        return Response.status(Response.Status.OK).entity(messageReceive).build()
    }

    @POST
    @Path("/register")
    fun deviceRegister(mobileUserAuth: MobileUserAuth): Response {
        return Response.status(Response.Status.OK).entity(mobileHttpRestService.registerMobile(mobileUserAuth)).build()
    }

    @GET
    @Path("/mobileauthtemplate")
    fun getMobileUserAuthPattern(): Response {
        val pcu = Pcu(UUID.randomUUID(), "999", "Template").apply {
            session = "klasjdfieklslkalskjfiejilejsf"
            lastKnownIp = "127.0.0.1"
        }
        val mobileUserAuth = MobileUserAuth("ADM", "MDA", UUID.randomUUID(), pcu)
        return Response.status(Response.Status.OK).entity(mobileUserAuth).build()
    }

    @GET
    @Path("/messagesynctemplate")
    fun getMessageSyncPattern(): Response {
        return Response.status(Response.Status.OK).entity(messagetemplate).build()
    }

    private val messagetemplate = Message(UUID.randomUUID(), UUID.randomUUID(), Message.Status.DEFAULT, Message.Action.DEFAULT)

}

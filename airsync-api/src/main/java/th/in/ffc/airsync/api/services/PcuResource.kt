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

package th.`in`.ffc.airsync.api.services

import ffc.model.Message
import ffc.model.MobileUserAuth
import ffc.model.Pcu
import th.`in`.ffc.airsync.api.dao.DaoFactory
import th.`in`.ffc.airsync.api.dao.GsonConvert
import th.`in`.ffc.airsync.api.services.module.MobileHttpRestService
import th.`in`.ffc.airsync.api.services.module.MobileServices
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Consumes
import javax.ws.rs.DefaultValue
import javax.ws.rs.GET
import javax.ws.rs.NotFoundException
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/pcu")

class PcuResource {
    companion object {
        val mobileHttpRestService = MobileHttpRestService()
    }

    val pcuDao = DaoFactory().buildPcuDao()

    @GET
    fun find(@Context req: HttpServletRequest,
             @DefaultValue("false") @QueryParam("mypcu") mypcu: Boolean = false): List<Pcu> {

        val pcuList = arrayListOf<Pcu>()
        if (mypcu) {
            pcuList.add(pcuDao.findByIpAddress(req.getRemoteAddr()))
        } else {
            pcuList.addAll(pcuDao.find())
        }

        if (pcuList.isEmpty()) {
            throw NotFoundException("Not pcu")
        }

        return pcuList
    }

    @POST
    fun post(@Context req: HttpServletRequest, message: Message): Response {
        var messageReceive = messagetemplate
        mobileHttpRestService.sendAndRecive(message, object : MobileServices.OnReceiveListener {
            override fun onReceive(messagestr: String) {
                println("Http POST pcu")
                messageReceive = GsonConvert.gson.fromJson(messagestr, Message::class.java)
            }

        })
        return Response.status(Response.Status.OK).entity(messageReceive).build()
    }

    @POST
    @Path("/deviceregister")
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

    private val messagetemplate = Message(UUID.randomUUID(), UUID.randomUUID(), -1, Message.Action.PING)

}

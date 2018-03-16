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
    fun post(@Context req: HttpServletRequest, message: Message<Any>): Response {
        var messageReturn: Message<MobileUserAuth> = messagetemplate
        println("Post mobile action = "+ message.action)


        if(message.action==Message.Action.REGISTER){ //ให้เพิ่มรอเช็คกับ Pcu ๗นเสร็จก่อน แล้วค่อย Returun
            println("Register Send to Pcu data = "+message.toJson())
            val mobileUserAuth: MobileUserAuth = message.data as MobileUserAuth
            println("Mobile auty user = "+mobileUserAuth.toJson())
            return Response.status(Response.Status.OK).entity(mobileHttpRestService.registerMobile(mobileUserAuth)).build()
        }


        else if(message.action==Message.Action.SENDTO){
            //ตรวจสอบเรื่องการ ค้นหาเเส้นทางก่อน  /อันนี้มันส่ง สดๆ ไม่ใช่ผ่่าน web Service ลองทำใหม่
            mobileHttpRestService.sendToPcu(message)


        }





        return Response.status(Response.Status.OK).entity(messageReturn).build()
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
    @Path("/msgauth")
    fun getMessageSyncPattern(): Response {
        return Response.status(Response.Status.OK).entity(messagetemplate).build()
    }

    private val messagetemplate = Message(UUID.randomUUID(), UUID.randomUUID(), Message.Status.DEFAULT, Message.Action.DEFAULT,MobileUserAuth("ADM","MDA",UUID.fromString("d57c809d-8ee8-4c8d-9a47-a4a7982a4768"), Pcu(UUID.fromString("00000000-0000-0000-0000-000000000009")),MobileUserAuth.UserStatus.VALIDATE))

}

package th.`in`.ffc.airsync.api.services

import th.`in`.ffc.airsync.api.dao.GsonConvert
import th.`in`.ffc.airsync.api.websocket.ApiSocket
import th.`in`.ffc.module.struct.obj.MessageSync
import th.`in`.ffc.module.struct.obj.PcuList
import th.`in`.ffc.module.struct.obj.mobiletoken.MobileToken
import th.`in`.ffc.module.struct.obj.mobiletoken.MobileUserAuth
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/pcu")


class Pcu {


    @GET
    fun get(@Context req: HttpServletRequest, @DefaultValue("false") @QueryParam("mypcu") mypcu: Boolean = false): Response {
        val pculist: PcuList = PcuList(ArrayList())

        if (mypcu) {
            pculist.pcuList.add(Connecter.register.findPcuByIpAddress(req.getRemoteAddr()))
        } else {
            pculist.pcuList.addAll(Connecter.register.getAllPcu())
        }


        return Response.status(Response.Status.OK).entity(pculist).build()
    }

    @POST
    fun post(@Context req: HttpServletRequest, message: MessageSync): Response {





        return Response.status(Response.Status.OK).build()
    }

    @POST
    @Path("/deviceregister")
    fun deviceRegister(mobileUserAuth: MobileUserAuth): Response {
        //val mobileToken = Connecter.register.findMobileByMobileToken(token.token)
        var strout: String = ""
        val pcu = Connecter.register.findPcuByUuid(mobileUserAuth.pcu.uuid)
        val pcuNetwork = ApiSocket.connectionMap.get(pcu.session)
        var messageSyncOut = MessageSync(UUID.randomUUID(),UUID.randomUUID(),-1,-1,"")
        if (pcuNetwork != null) {
            val messageSync = MessageSync(UUID.fromString(mobileUserAuth.mobileUuid.toString()), UUID.fromString(mobileUserAuth.pcu.uuid.toString()), 0, 1, GsonConvert.gson.toJson(mobileUserAuth))
            var waitToDevice = true
            var count = 0

            ApiSocket.mobileHashMap.put(mobileUserAuth.mobileUuid, object : ApiSocket.onReciveMessage{
                override fun setOnReceiveMessage(message: String) {
                    strout = message
                    waitToDevice = false
                    messageSyncOut = GsonConvert.gson.fromJson(message,MessageSync::class.java)
                    if(messageSyncOut.status==200){
                        val mobileUserAuthReceive = GsonConvert.gson.fromJson(messageSyncOut.message,MobileUserAuth::class.java)
                        Connecter.register.registerMobile(MobileToken(UUID.fromString(messageSyncOut.to.toString()),mobileUserAuthReceive.pcu))
                        println("Register Mobile "+ messageSyncOut.to.toString())
                    }

                }
            })
            pcuNetwork.session.remote.sendString(GsonConvert.gson.toJson(messageSync))



            while (waitToDevice && count < 10) {
                count++
                Thread.sleep(2000)
                println("Wait Count "+count)
            }
        }

        return Response.status(Response.Status.OK).entity(messageSyncOut).build()
    }


    @GET
    @Path("/mobileauthtemplate")
    fun getMobileUserAuthPattern(): Response {
        val mobileUserAuth = MobileUserAuth("ADM", "MDA", UUID.randomUUID(), th.`in`.ffc.module.struct.obj.Pcu("999", "Template", UUID.randomUUID(), "klasjdfieklslkalskjfiejilejsf", "127.0.0.1"))
        return Response.status(Response.Status.OK).entity(mobileUserAuth).build()
    }
}

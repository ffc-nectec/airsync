package th.`in`.ffc.airsync.api.services


import th.`in`.ffc.airsync.api.dao.GsonConvert
import th.`in`.ffc.airsync.api.services.module.MobileHttpRestService
import th.`in`.ffc.airsync.api.services.module.MobileServices
import th.`in`.ffc.module.struct.obj.MessageSync
import th.`in`.ffc.module.struct.obj.PcuList
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
    companion object {
        val mobileHttpRestService = MobileHttpRestService()
    }


    @GET
    fun get(@Context req: HttpServletRequest, @DefaultValue("false") @QueryParam("mypcu") mypcu: Boolean = false): Response {
        val pculist: PcuList = PcuList(ArrayList())

        if (mypcu) {
            pculist.pcuList.addAll(mobileHttpRestService.getMyPcu(req.getRemoteAddr()))
        } else {
            pculist.pcuList.addAll(mobileHttpRestService.getAll())
        }
        if(pculist.pcuList.size<1){
            throw NotFoundException("Not pcu")
        }

        return Response.status(Response.Status.OK).entity(pculist).build()
    }

    @POST
    fun post(@Context req: HttpServletRequest, message: MessageSync): Response {
        var messageReceive = getMessagetemplate()
        mobileHttpRestService.sendAndRecive(message,object : MobileServices.OnReceiveListener{
            override fun onReceive(messagestr: String) {
                println("Http POST pcu")
                messageReceive = GsonConvert.gson.fromJson(messagestr,MessageSync::class.java)
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
        val mobileUserAuth = MobileUserAuth("ADM", "MDA", UUID.randomUUID(), th.`in`.ffc.module.struct.obj.Pcu("999", "Template", UUID.randomUUID(), "klasjdfieklslkalskjfiejilejsf", "127.0.0.1"))
        return Response.status(Response.Status.OK).entity(mobileUserAuth).build()
    }

    @GET
    @Path("/messagesynctemplate")
    fun getMessageSyncPattern(): Response {
        return Response.status(Response.Status.OK).entity(getMessagetemplate()).build()
    }
    private fun getMessagetemplate(): MessageSync{
        return MessageSync(UUID.randomUUID(), UUID.randomUUID(),-1,10)
    }


}

package th.`in`.ffc.airsync.api.services

import th.`in`.ffc.airsync.api.dao.GsonConvert
import th.`in`.ffc.airsync.api.services.module.MobileHttpRestService
import th.`in`.ffc.airsync.api.services.module.MobileServices
import th.`in`.ffc.module.struct.obj.MessageSync
import th.`in`.ffc.module.struct.obj.Pcu
import th.`in`.ffc.module.struct.obj.mobiletoken.MobileUserAuth
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

    @GET
    fun get(@Context req: HttpServletRequest, @DefaultValue("false") @QueryParam("mypcu") mypcu: Boolean = false): Response {
        val pcuList = arrayListOf<Pcu>()
        if (mypcu) {
            pcuList.addAll(mobileHttpRestService.getMyPcu(req.getRemoteAddr()))
        } else {
            pcuList.addAll(mobileHttpRestService.getAll())
        }

        if (pcuList.isEmpty()) {
            throw NotFoundException("Not pcu")
        }

        return Response.status(Response.Status.OK).entity(pcuList).build()
    }

    @POST
    fun post(@Context req: HttpServletRequest, message: MessageSync): Response {
        var messageReceive = messagetemplate
        mobileHttpRestService.sendAndRecive(message, object : MobileServices.OnReceiveListener {
            override fun onReceive(messagestr: String) {
                println("Http POST pcu")
                messageReceive = GsonConvert.gson.fromJson(messagestr, MessageSync::class.java)
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

    private val messagetemplate = MessageSync(UUID.randomUUID(), UUID.randomUUID(), -1, MessageSync.Action.PING)

}

package th.`in`.ffc.airsync.api.services

import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/pcuaction")
class PcuAction {
    @GET
    fun get(@Context req: HttpServletRequest, @DefaultValue("00000000-0000-0000-0000-000000000000") @QueryParam("pcuUuid") pcuUuid: UUID) :Response{
        val pcu = Connecter.connecter.findByUuid(pcuUuid)
        return Response.status(Response.Status.OK).entity(Connecter.connecter.getPcuAction(pcu)).build()
    }

    @POST
    fun post(){

    }
}

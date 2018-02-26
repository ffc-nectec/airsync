package th.`in`.ffc.airsync.api.services

import th.`in`.ffc.module.struct.MobileToken
import th.`in`.ffc.module.struct.QueryAction
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/device")
class DeviceAction {
    @GET
    fun get(){

    }

    @POST
    @Path("/topcu")
    fun post(@Context req: HttpServletRequest, @DefaultValue("00000000-0000-0000-0000-000000000000") @QueryParam("token") tricket: UUID,queryAction: QueryAction){
        Connecter.connecter.sendToPcu(tricket,queryAction)
    }

    @GET
    @Path("/register")
    fun regis(@Context req: HttpServletRequest) :Response{

        val ffcDevice= MobileToken(UUID.fromString("00000000-0000-0000-0000-000000000000"),Connecter.connecter.getAllPcu().pcuList.get(0))

        Connecter.connecter.mapDevice(ffcDevice)

        return Response.status(Response.Status.OK).entity(ffcDevice).build()

    }

    @GET
    @Path("/template")
    fun template() :Response{
        val qa = QueryAction(ArrayList(), UUID.randomUUID())
        qa.sqlQuery.add("rmove delete")
        qa.sqlQuery.add("delete remove")
        qa.sqlQuery.add("insert table")

        return Response.status(Response.Status.OK).entity(qa).build()
    }
}

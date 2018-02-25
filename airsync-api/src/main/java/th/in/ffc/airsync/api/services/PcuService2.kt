package th.`in`.ffc.airsync.api.services

import th.`in`.ffc.module.struct.Pcu
import th.`in`.ffc.module.struct.PcuList
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import kotlin.collections.ArrayList

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/pcu2")
class PcuService2 {


    @GET
    fun get(@Context req: HttpServletRequest, @DefaultValue("false") @QueryParam("mypcu") mypcu: Boolean): Response {

        //080271a9-43ea-40e3-830f-c776a015d61b
        var list:PcuList = PcuList(ArrayList<Pcu>())

        try {
            if (mypcu) {
                val listtemp = ArrayList<Pcu>()
                listtemp.add((Connecter.connecter.findByIpAddress(req.remoteAddr)))

                if (listtemp.size > 0) {
                    list.pcuList = listtemp
                }

            } else {
                list = Connecter.connecter.getAllPcu()


            }
        } catch (e: kotlin.NotImplementedError) {

        }
        if (list.pcuList.size < 1) {
            throw NotFoundException()
        }
        return Response.status(Response.Status.OK).entity(list).build()
    }

    @POST
    fun post(@Context req: HttpServletRequest, pcu: Pcu): Response {
        print("Before = "+ Connecter.connecter.getAllPcu().pcuList.size)
        Connecter.connecter.insert(pcu, req.getRemoteAddr())
        println("After = "+ Connecter.connecter.getAllPcu().pcuList.size)
        val out = "Pcu Register getRemoteAddr=" + req.remoteAddr + " getRemoteHost=" + req.remoteHost + " "
        println(out)
        //var list : Pcu = Connecter.connecter.getAllPcu().get(1)
        //println(list.Code+"\t"+list.Name+"\t"+list.uuid+"\t"+list.haveWork)
        return Response.status(Response.Status.OK).build()
    }

    @GET
    @Path("/template")
    fun getTemplate(): Response {
        var pcu = Pcu("939923", "nectec", UUID.randomUUID(), false)
        return Response.status(Response.Status.OK).entity(pcu).build()

    }
}

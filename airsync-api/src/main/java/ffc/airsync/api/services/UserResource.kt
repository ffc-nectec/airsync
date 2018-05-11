package ffc.airsync.api.services

import ffc.airsync.api.services.module.UserService
import ffc.model.User
import ffc.model.printDebug
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.xml.bind.DatatypeConverter


@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/org")
class UserResource {

    @POST
    @Path("/{orgUuid:([\\dabcdefABCDEF].*)}/user")
    fun create(@Context req: HttpServletRequest,
               @PathParam("orgUuid") orgId: String,
               userList: ArrayList<User>): Response {
        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")


        printDebug("Raw user list.")
        userList.forEach {
            printDebug("User = " + it.username + " Pass = " + it.password)
        }

        UserService.create(token, orgId, userList)
        return Response.status(Response.Status.CREATED).build()


    }


    @POST
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/authorize")
    fun registerMobile(@Context req: HttpServletRequest,
                       @PathParam("orgId") orgId: String): Response {

        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Basic ", "")
          ?: throw NotAuthorizedException("Not Authorization")
        val userpass = DatatypeConverter.parseBase64Binary(token).toString(charset("UTF-8")).split(":")
        val user = userpass.get(index = 0)
        val pass = userpass.get(index = 1)


        printDebug("Mobile Login Auid = " + orgId +
          " User = " + user +
          " Pass = " + pass)
        val tokenMessage = UserService.checkAuth(orgId, user, pass)


        printDebug("Token is $tokenMessage")
        return Response.status(Response.Status.CREATED).entity(tokenMessage).build()
    }

}

package ffc.airsync.api.services

import ffc.airsync.api.services.module.FirebaseService
import ffc.model.FirebaseToken
import ffc.model.printDebug
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/org")
class FirebaseResource {

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{orgId:([\\dabcdefABCDEF].*)}/firebase")
    fun updateToken(@Context req: HttpServletRequest,
                    @PathParam("orgId") orgId: String,
                    firebaseToken: FirebaseToken): Response {

        printDebug("Call update Firebase Token by ip = " + req.remoteAddr + " OrgID $orgId Firebase Token = ${firebaseToken.firebasetoken}")

        val httpHeader = req.buildHeaderMap()
        val token = httpHeader["Authorization"]?.replaceFirst("Bearer ", "")
          ?: throw NotAuthorizedException("Not Authorization")


        FirebaseService.updateToken(UUID.fromString(token), orgId, firebaseToken)

        return Response.status(200).build()
    }
}

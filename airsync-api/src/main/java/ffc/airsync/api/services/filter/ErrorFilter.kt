package ffc.airsync.api.services.filter

import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class ErrorFilter : ExceptionMapper<WebApplicationException> {

    override fun toResponse(exception: WebApplicationException?): Response {
        exception!!.printStackTrace()
        //val err = ErrorRes(exception.response.status, exception.message, exception)
        return Response.status(exception.response.status).entity(exception.message).build()

    }

    data class ErrorRes(val code: Int, val message: String?, val t: Throwable)
}



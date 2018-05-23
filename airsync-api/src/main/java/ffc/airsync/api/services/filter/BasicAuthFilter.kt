package ffc.airsync.api.services.filter

import javax.ws.rs.NotAuthorizedException
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter


class BasicAuthFilter : ContainerRequestFilter {
    val AUTHORIZATION_PROPERTY = "Authorization"
    val AUTHENTICATION_SCHEME = "Bearer"


    override fun filter(requestContext: ContainerRequestContext) {
        val authenInfo = BasicAuthInfo(requestContext)

    }


    class BasicAuthInfo(requestContext: ContainerRequestContext) {


        init {

        }


    }

    fun getAuthorizeProperty(requestContext: ContainerRequestContext): List<String> {
        val authorization = requestContext!!.headers.get(AUTHORIZATION_PROPERTY)
        if (authorization == null || authorization.isEmpty()) {
            throw NotAuthorizedException("ไม่มีข้อมูล Auth")
        }

        return authorization
    }
}




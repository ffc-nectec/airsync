package ffc.airsync.api.services.filter

import ffc.model.TokenMessage
import java.security.Principal
import javax.ws.rs.core.SecurityContext

class OrgSecurityContextImp : SecurityContext {

    private var HTTPS = "https://"
    private var userPrincipal: Principal? = null
    private var scheme: String? = null


    constructor(token: TokenMessage, scheme: String) {

        this.scheme = scheme

        this.userPrincipal = object : Principal {
            override fun getName(): String {
                return token.name ?: ""
            }

        }

    }



    override fun isUserInRole(role: String?): Boolean {
        return TokenMessage.TYPERULE.ORG.toString().equals(role)
    }

    override fun getAuthenticationScheme(): String {
        return "Bearer"
    }

    override fun getUserPrincipal(): Principal {
        return userPrincipal!!
    }

    override fun isSecure(): Boolean {
        return true
    }
}

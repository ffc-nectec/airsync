package ffc.airsync.api.services.filter

import java.security.Principal
import javax.ws.rs.core.SecurityContext

class OrgSecurityContextImp : SecurityContext {
    private var HTTPS = "https://"
    private var userPrincipal: Principal? = null
    private var scheme: String? = null


    override fun isUserInRole(role: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAuthenticationScheme(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserPrincipal(): Principal {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isSecure(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

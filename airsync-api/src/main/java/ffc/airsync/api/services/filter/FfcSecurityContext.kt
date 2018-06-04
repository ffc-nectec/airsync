package ffc.airsync.api.services.filter

import ffc.model.TokenMessage
import java.util.*
import javax.ws.rs.core.SecurityContext

interface FfcSecurityContext : SecurityContext {
    val token: TokenMessage?
    val orgId: String?
}

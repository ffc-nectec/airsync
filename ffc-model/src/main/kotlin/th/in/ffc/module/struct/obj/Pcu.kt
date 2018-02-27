package th.`in`.ffc.module.struct.obj

import org.eclipse.jetty.websocket.api.Session
import java.util.*
import kotlin.collections.HashMap

data class Pcu(val Code: String, val Name: String, val uuid: UUID,var session: String,var ipaddress:String)

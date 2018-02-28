package th.`in`.ffc.module.struct.obj.mobiletoken

import th.`in`.ffc.module.struct.obj.Pcu
import java.util.*

data class MobileUserAuth(val username: String,
                          val password: String,
                          val mobileUuid: UUID,
                          val pcu: Pcu)

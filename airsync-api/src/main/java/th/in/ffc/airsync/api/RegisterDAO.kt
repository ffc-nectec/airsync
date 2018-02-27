package th.`in`.ffc.airsync.api

import th.`in`.ffc.module.struct.obj.mobiletoken.MobileToken
import th.`in`.ffc.module.struct.obj.Pcu
import java.util.*

interface RegisterDAO {
    fun registerPcu(pcu : Pcu)
    fun findPcuByUuid(uuid: UUID): Pcu
    fun findPcuByIpAddress(ipAddress: String): Pcu

    fun unregisterPcu(pcu: Pcu)

    fun registerMobile(device: MobileToken)
    fun findMobileByMobileToken(token :UUID) : MobileToken

    fun unregisterMobile(device: MobileToken)

    fun getAllPcu() :List<Pcu>
    fun countPcu(): Int
}

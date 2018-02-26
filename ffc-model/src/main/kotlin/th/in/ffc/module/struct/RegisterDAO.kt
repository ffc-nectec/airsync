package th.`in`.ffc.module.struct

import java.util.*

interface RegisterDAO {
    fun registerPcu(pcu :Pcu)
    fun findPcuByUuid(uuid: UUID): Pcu
    fun findPcuByIpAddress(ipAddress: String): Pcu

    fun unregisterPcu(pcu: Pcu)

    fun registerMobile(device: MobileToken)
    fun findMobileByMobileToken(token :UUID) : MobileToken

    fun unregisterMobile(device: MobileToken)

    fun getAllPcu() :PcuList
    fun countPcu(): Int
}

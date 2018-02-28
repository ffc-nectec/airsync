package th.`in`.ffc.airsync.api.dao

import th.`in`.ffc.module.struct.obj.Pcu
import java.util.*

interface PcuDao {
    fun insert(pcu: Pcu)
    fun find(): List<Pcu>
    fun findByUuid(uuid: UUID): Pcu
    fun findByIpAddress(ipAddress: String): Pcu
    fun remove(pcu: Pcu)
}

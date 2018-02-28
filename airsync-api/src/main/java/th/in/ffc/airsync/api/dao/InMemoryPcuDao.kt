package th.`in`.ffc.airsync.api.dao

import th.`in`.ffc.module.struct.obj.Pcu
import java.util.*

class InMemoryPcuDao : PcuDao {

    private constructor()

    val pcuList = arrayListOf<Pcu>()

    companion object {
        val instance = InMemoryPcuDao()
    }

    override fun insert(pcu: Pcu) {
        if (!pcuList.contains(pcu))
            pcuList.add(pcu)
    }

    override fun findByUuid(uuid: UUID): Pcu {
        return pcuList.find { it.uuid == uuid }!!
    }

    override fun findByIpAddress(ipAddress: String): Pcu {
        return pcuList.find { it.lastKnownIp == ipAddress }!!
    }

    override fun remove(pcu: Pcu) {
        pcuList.remove(pcu)
    }

    override fun find(): List<Pcu> {
        return pcuList.toList()
    }
}

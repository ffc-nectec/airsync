package th.`in`.ffc.module.struct.interfa

import th.`in`.ffc.module.struct.obj.mobiletoken.MobileToken
import th.`in`.ffc.module.struct.obj.Pcu
import th.`in`.ffc.module.struct.obj.PcuList
import th.`in`.ffc.module.struct.obj.QueryAction
import java.util.*
import kotlin.collections.ArrayList


interface PcuDataAccessObject {
    fun insert(pcu : Pcu, ipAddress: String)
    fun insertDevice(device: MobileToken)
    fun heartbeatTrick(uuid: UUID): Pcu
    fun findByUuid(uuid: UUID): Pcu
    fun findByIpAddress(ipAddress: String): Pcu
    fun findByDeviceTicket(tricket: UUID): MobileToken
    fun getAllPcu() : PcuList
    fun getSize(): Int

    fun mapDevice(mobileToken: MobileToken)
    fun sendToPcu(tricket: UUID,queryAction: QueryAction)
    fun getPcuAction(pcu: Pcu): ArrayList<QueryAction>
    fun sendToDevice(pcu: Pcu)
    fun getDeviceAction(tricket: UUID): QueryAction

}

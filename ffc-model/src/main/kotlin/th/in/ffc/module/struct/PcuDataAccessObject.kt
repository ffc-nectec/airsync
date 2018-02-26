package th.`in`.ffc.module.struct

import org.eclipse.jetty.websocket.api.Session
import java.util.*
import kotlin.collections.ArrayList


interface PcuDataAccessObject {
    fun insert(pcu :Pcu, ipAddress: String)
    fun insertDevice(device: MobileToken)
    fun heartbeatTrick(uuid: UUID): Pcu
    fun findByUuid(uuid: UUID): Pcu
    fun findByIpAddress(ipAddress: String): Pcu
    fun findByDeviceTicket(tricket: UUID): MobileToken
    fun getAllPcu() :PcuList
    fun getSize(): Int

    fun mapDevice(mobileToken: MobileToken)
    fun sendToPcu(tricket: UUID,queryAction: QueryAction)
    fun getPcuAction(pcu: Pcu): ArrayList<QueryAction>
    fun sendToDevice(pcu: Pcu)
    fun getDeviceAction(tricket: UUID): QueryAction

}

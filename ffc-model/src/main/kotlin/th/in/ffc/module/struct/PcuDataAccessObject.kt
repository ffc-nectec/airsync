package th.`in`.ffc.module.struct

import java.util.*
import kotlin.collections.ArrayList


interface PcuDataAccessObject {
    fun insert(pcu :Pcu, ipAddress: String)
    fun insertDevice(device: FfcDevice)
    fun heartbeatTrick(uuid: UUID): Pcu
    fun findByUuid(uuid: UUID): Pcu
    fun findByIpAddress(ipAddress: String): Pcu
    fun findByDeviceTicket(tricket: UUID): FfcDevice
    fun getAllPcu() :PcuList
    fun getSize(): Int

    fun mapDevice(ffcDevice: FfcDevice)
    fun sendToPcu(tricket: UUID,queryAction: QueryAction)
    fun getPcuAction(pcu: Pcu): ArrayList<QueryAction>
    fun sendToDevice(pcu: Pcu)
    fun getDeviceAction(tricket: UUID): QueryAction

}

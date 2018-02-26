package th.`in`.ffc.airsync.api.dao

import th.`in`.ffc.module.struct.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("UNREACHABLE_CODE")
class HashMapsConnecter :PcuDataAccessObject{


    companion object {
        var ipMaps = HashMap<String,Pcu>()
        var uuidMaps = HashMap<UUID,Pcu>()
        var deviceMaps = HashMap<UUID,FfcDevice>()
        var pcuAction = HashMap<UUID,HashMap<UUID,QueryAction>>()

    }

    override fun mapDevice(ffcDevice: FfcDevice) {
        deviceMaps.put(ffcDevice.tricket,ffcDevice)
    }
    override fun sendToPcu(tricket: UUID, queryAction: QueryAction) {
        val device = findByDeviceTicket(tricket)


        var hashAction : HashMap<UUID,QueryAction> = HashMap<UUID,QueryAction>()

        try{
            hashAction=pcuAction.getValue(device.pcu.uuid)
        }catch (e :NoSuchElementException){
            hashAction= HashMap<UUID,QueryAction>()/////////////----------------------Remove
        }finally {
            hashAction.put(queryAction.uuidAction,queryAction)
        }



        pcuAction.put(device.pcu.uuid,hashAction)
    }

    override fun getPcuAction(pcu: Pcu): ArrayList<QueryAction> {
        val queryList : ArrayList<QueryAction> =ArrayList()
        pcuAction.getValue(pcu.uuid).forEach { uuid, action -> queryList.add(action) }

        return queryList
    }

    override fun sendToDevice(pcu: Pcu) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDeviceAction(tricket: UUID): QueryAction {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



    override fun getSize(): Int {

        //To change body of created functions use File | Settings | File Templates.
        return uuidMaps.size
    }


    override fun getAllPcu(): PcuList {
        //To change body of created functions use File | Settings | File Templates.

        val list = ArrayList<Pcu>()

        uuidMaps.forEach { uuid, pcu ->  list.add(pcu)}


        return PcuList(list)

    }

    override fun heartbeatTrick(uuid: UUID): Pcu {
        //To change body of created functions use File | Settings | File Templates.
        return findByUuid(uuid)
    }

    override fun insert(pcu: Pcu, ipAddress: String) {
        //To change body of created functions use File | Settings | File Templates.
        ipMaps.put(ipAddress,pcu)
        uuidMaps.put(pcu.uuid,pcu)
    }

    override fun insertDevice(device: FfcDevice) {
         //To change body of created functions use File | Settings | File Templates.
        deviceMaps.put(device.tricket,device)

    }

    override fun findByUuid(uuid: UUID): Pcu {
        //To change body of created functions use File | Settings | File Templates.
        return uuidMaps.getValue(uuid)
    }

    override fun findByIpAddress(ipAddress: String): Pcu {
        return ipMaps.getValue(ipAddress)
    }

    override fun findByDeviceTicket(tricket: UUID): FfcDevice {
        return deviceMaps.getValue(tricket)
    }

}

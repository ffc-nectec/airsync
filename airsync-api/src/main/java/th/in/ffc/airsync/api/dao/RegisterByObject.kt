package th.`in`.ffc.airsync.api.dao

import com.google.gson.Gson
import th.`in`.ffc.module.struct.obj.Pcu
import th.`in`.ffc.module.struct.obj.mobiletoken.MobileToken
import java.util.*
import kotlin.collections.HashMap

class RegisterByObject : RegisterDAO {

    companion object {
        var clientAirSync = HashMap<String, String>()
        var clientIpAddress = HashMap<String, String>()
        var clientSession = HashMap<String, String>()
        var clientMobile = HashMap<String, String>()


        var gson = Gson()
    }

    override fun registerPcu(pcu: Pcu) {
        clientAirSync.put(pcu.uuid.toString(), gson.toJson(pcu))
        clientIpAddress.put(pcu.ipaddress, gson.toJson(pcu))
        clientSession.put(pcu.session, gson.toJson(pcu))
    }

    override fun findPcuByUuid(uuid: UUID): Pcu {
        return gson.fromJson(clientAirSync.get(uuid.toString()), Pcu::class.java)
    }

    override fun findPcuByIpAddress(ipAddress: String): Pcu {
        return gson.fromJson(clientIpAddress.get(ipAddress), Pcu::class.java)
    }

    override fun unregisterPcu(pcu: Pcu) {
        clientAirSync.remove(pcu.uuid.toString())
        clientIpAddress.remove(pcu.ipaddress)
        clientSession.remove(pcu.session)
    }

    override fun registerMobile(device: MobileToken) {
        clientMobile.put(device.token.toString(), gson.toJson(device))
    }

    override fun findMobileByMobileToken(token: UUID): MobileToken {
        return gson.fromJson(clientMobile.get(token.toString()), MobileToken::class.java)
    }

    override fun unregisterMobile(device: MobileToken) {
        clientMobile.remove(device.token.toString())
    }

    override fun getAllPcu(): List<Pcu> {
        val litpcu = ArrayList<Pcu>()
        clientAirSync.forEach { key, data -> litpcu.add(gson.fromJson(data, Pcu::class.java)) }
        return litpcu
    }

    override fun countPcu(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

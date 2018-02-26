package th.`in`.ffc.airsync.api

import com.google.gson.Gson
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.transport.client.PreBuiltTransportClient
import th.`in`.ffc.module.struct.MobileToken
import th.`in`.ffc.module.struct.Pcu
import th.`in`.ffc.module.struct.PcuList
import th.`in`.ffc.module.struct.RegisterDAO
import java.net.InetAddress
import java.util.*

class RegisterElasticSearch : RegisterDAO {
    companion object {
        var client :TransportClient= PreBuiltTransportClient(Settings.EMPTY)
          .addTransportAddress(TransportAddress(InetAddress.getByName("127.0.0.1"),9300))
        var gson = Gson()

    }
    override fun registerPcu(pcu: Pcu) {
        insertElastic("airsync","air",pcu.uuid.toString(),gson.toJson(pcu))
        insertElastic("ipaddress","ip",pcu.ipaddress,gson.toJson(pcu))
        insertElastic("session","sess",pcu.session,gson.toJson(pcu))

    }

    override fun unregisterPcu(pcu: Pcu) {
        deleteElastic("airsync","air",pcu.uuid.toString())
        deleteElastic("ipaddress","ip",pcu.ipaddress)
        deleteElastic("session","sess",pcu.session)
    }

    override fun findPcuByUuid(uuid: UUID): Pcu {
        var response = getElastic("airsync","air",uuid.toString())
        println(response.sourceAsString)
        return gson.fromJson(response.sourceAsString,Pcu::class.java)
    }


    override fun findPcuByIpAddress(ipAddress: String): Pcu {
        var response = getElastic("ipaddress","ip",ipAddress)
        println(response.sourceAsString)
        return gson.fromJson(response.sourceAsString,Pcu::class.java)
    }


    override fun registerMobile(device: MobileToken) {
        insertElastic("mobile","token",device.token.toString(), gson.toJson(device))
    }

    override fun unregisterMobile(device: MobileToken) {
        deleteElastic("mobile","token",device.token.toString())
    }

    override fun findMobileByMobileToken(token: UUID): MobileToken {
        var response = getElastic("mobile","token",token.toString())
        return gson.fromJson(response.sourceAsString,MobileToken::class.java)
    }

    override fun getAllPcu(): PcuList {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun countPcu(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



    private fun insertElastic(index:String,type:String , id :String ,json :String):IndexResponse{
        return client.prepareIndex(index, type,id)
          .setSource(json, XContentType.JSON)
          .get()
    }

    private fun getElastic(index:String,type:String , id :String) :GetResponse {
        return client.prepareGet(index, type,id).get()
    }

    private fun deleteElastic(index:String,type:String , id :String) :DeleteResponse{
        return client.prepareDelete(index, type,id).get()
    }

}

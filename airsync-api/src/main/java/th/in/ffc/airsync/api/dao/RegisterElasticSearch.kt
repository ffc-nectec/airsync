package th.`in`.ffc.airsync.api.dao

import com.google.gson.Gson
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.transport.client.PreBuiltTransportClient
import th.`in`.ffc.module.struct.obj.mobiletoken.MobileToken
import th.`in`.ffc.module.struct.obj.Pcu
import java.net.InetAddress
import java.util.*
import kotlin.collections.ArrayList

class RegisterElasticSearch : RegisterDAO {
    companion object {
        var client :TransportClient= PreBuiltTransportClient(Settings.EMPTY)
          .addTransportAddress(TransportAddress(InetAddress.getByName("127.0.0.1"),9300))
        var gson = Gson()

    }
    override fun registerPcu(pcu: Pcu) {
        insertElastic("airsync","air",pcu.uuid.toString(), gson.toJson(pcu))
        insertElastic("ipaddress","ip",pcu.ipaddress, gson.toJson(pcu)) //อนาคตต้องเก็บเป็นแบบ list ป้องกัน IP ซ้ำ   ดึงค่ามาอ่าน ตรวจสอบ uuid ถ้าซ้ำ update ถ้าคนละ uuid ให้เพิ่มใน List
        insertElastic("session","sess",pcu.session, gson.toJson(pcu))

    }

    override fun unregisterPcu(pcu: Pcu) {
        deleteElastic("airsync","air",pcu.uuid.toString())
        deleteElastic("ipaddress","ip",pcu.ipaddress)
        deleteElastic("session","sess",pcu.session)
    }

    override fun findPcuByUuid(uuid: UUID): Pcu {
        var response = getElastic("airsync","air",uuid.toString())
        println(response.sourceAsString)
        return gson.fromJson(response.sourceAsString, Pcu::class.java)
    }


    override fun findPcuByIpAddress(ipAddress: String): Pcu {
        var response = getElastic("ipaddress","ip",ipAddress)
        println(response.sourceAsString)
        return gson.fromJson(response.sourceAsString, Pcu::class.java)
    }


    override fun registerMobile(device: MobileToken) {
        insertElastic("mobile","token",device.token.toString(), gson.toJson(device))
    }

    override fun unregisterMobile(device: MobileToken) {
        deleteElastic("mobile","token",device.token.toString())
    }

    override fun findMobileByMobileToken(token: UUID): MobileToken {
        val response = getElastic("mobile","token",token.toString())
        return gson.fromJson(response.sourceAsString, MobileToken::class.java)
    }

    override fun getAllPcu(): List<Pcu> {

        var response =searchElastic("airsync","air")
        val litpcu = ArrayList<Pcu>()
        response.hits.hits.forEach { result -> litpcu.add(gson.fromJson(result.sourceAsString, Pcu::class.java)) }
        return litpcu
    }

    override fun countPcu(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



    private fun insertElastic(index:String,type:String , id :String ,json :String):IndexResponse{
        return client
          .prepareIndex(index, type,id)
          .setSource(json, XContentType.JSON)
          .get()
    }

    private fun getElastic(index:String,type:String , id :String) :GetResponse {
        return client
          .prepareGet(index, type,id)
          .get()
    }

    private fun deleteElastic(index:String,type:String , id :String) :DeleteResponse{
        return client
          .prepareDelete(index, type,id)
          .get()
    }

    private fun searchElastic(index:String,type:String ) :SearchResponse{
        return client
          .prepareSearch(index)
          .setTypes(type)
          .setSearchType(SearchType.DFS_QUERY_THEN_FETCH).get()
    }

}

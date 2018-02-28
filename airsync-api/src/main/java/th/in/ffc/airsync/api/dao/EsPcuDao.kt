package th.`in`.ffc.airsync.api.dao

import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient
import th.`in`.ffc.module.struct.obj.Pcu
import java.net.InetAddress
import java.util.*
import kotlin.collections.ArrayList

class EsPcuDao : PcuDao {

    var client: TransportClient = PreBuiltTransportClient(Settings.EMPTY)
      .addTransportAddress(TransportAddress(InetAddress.getByName("127.0.0.1"), 9300))

    override fun insert(pcu: Pcu) {
        client.insert("airsync", "air", pcu.uuid.toString(), pcu.toJson())
        pcu.lastKnownIp?.let { client.insert("lastKnownIp", "ip", it, pcu.toJson()) }//อนาคตต้องเก็บเป็นแบบ list ป้องกัน IP ซ้ำ   ดึงค่ามาอ่าน ตรวจสอบ uuid ถ้าซ้ำ update ถ้าคนละ uuid ให้เพิ่มใน List
        pcu.session?.let { client.insert("session", "sess", it, pcu.toJson()) }
    }

    override fun remove(pcu: Pcu) {
        client.delete("airsync", "air", pcu.uuid.toString())

        pcu.lastKnownIp?.let { client.delete("lastKnownIp", "ip", it) }
        pcu.session?.let { client.delete("session", "sess", it) }
    }

    override fun findByUuid(uuid: UUID): Pcu {
        var response = client.get("airsync", "air", uuid.toString())
        println(response.sourceAsString)
        return response.sourceAsString.fromJson()
    }

    override fun findByIpAddress(ipAddress: String): Pcu {
        var response = client.get("lastKnownIp", "ip", ipAddress)
        println(response.sourceAsString)
        return response.sourceAsString.fromJson()
    }

    override fun find(): List<Pcu> {
        var response = client.search("airsync", "air")
        val pcuList = ArrayList<Pcu>()

        response.hits.hits.forEach { result -> pcuList.add(result.sourceAsString.fromJson()) }
        return pcuList
    }

}


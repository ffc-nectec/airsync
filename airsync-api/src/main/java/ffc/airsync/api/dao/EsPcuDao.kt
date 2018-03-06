/*
 * Copyright (c) 2561 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.airsync.api.dao

import ffc.model.Pcu
import ffc.model.fromJson
import ffc.model.toJson
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient
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
        pcu.pcuToken?.let { client.insert("pcuToken","pcuToken" ,it,pcu.toJson() ) }
    }

    override fun remove(pcu: Pcu) {
        client.delete("airsync", "air", pcu.uuid.toString())

        pcu.lastKnownIp?.let { client.delete("lastKnownIp", "ip", it) }
        pcu.session?.let { client.delete("session", "sess", it) }
        pcu.pcuToken?.let { client.delete("pcuToken","pcuToken" ,it) }
    }

    override fun findByUuid(uuid: UUID): Pcu {
        var response = client.get("airsync", "air", uuid.toString())
        println(response.sourceAsString)
        return response.sourceAsString.fromJson()
    }

    override fun findByToken(token: String): Pcu {
        val response = client.get("pcuToken","pcuToken",token)
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


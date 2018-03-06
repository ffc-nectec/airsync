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

import ffc.model.Mobile
import ffc.model.fromJson
import ffc.model.toJson
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient
import java.net.InetAddress
import java.util.*

class EsMobileDao : MobileDao {
    var client: TransportClient = PreBuiltTransportClient(Settings.EMPTY)
      .addTransportAddress(TransportAddress(InetAddress.getByName("127.0.0.1"), 9300))

    override fun insert(mobile: Mobile) {
        client.insert("mobile", "pcuToken", mobile.token.toString(), mobile.toJson())
    }

    override fun remove(mobile: Mobile) {
        client.delete("mobile", "pcuToken", mobile.token.toString())
    }

    override fun findByUuid(uuid: UUID): Mobile {
        val response = client.get("mobile", "uuid", uuid.toString())
        return response.sourceAsString.fromJson()
    }
}

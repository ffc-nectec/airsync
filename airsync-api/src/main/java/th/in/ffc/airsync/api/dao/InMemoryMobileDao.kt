/*
 * Copyright (c) 2018 NECTEC
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

package th.`in`.ffc.airsync.api.dao

import th.`in`.ffc.module.struct.obj.mobiletoken.Mobile
import java.util.*

class InMemoryMobileDao : MobileDao {

    private constructor()

    var clientMobile = HashMap<String, String>()

    companion object {
        val instance = InMemoryMobileDao()
    }

    override fun insert(mobile: Mobile) {
        clientMobile.put(mobile.token.toString(), mobile.toJson())
    }

    override fun findByUuid(uuid: UUID): Mobile {
        return clientMobile.get(uuid.toString())!!.fromJson()
    }

    override fun remove(mobile: Mobile) {
        clientMobile.remove(mobile.token.toString())
    }
}

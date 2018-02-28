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

import ffc.model.Pcu
import java.util.*

class InMemoryPcuDao : PcuDao {

    private constructor()

    val pcuList = arrayListOf<Pcu>()

    companion object {
        val instance = InMemoryPcuDao()
    }

    override fun insert(pcu: Pcu) {
        if (!pcuList.contains(pcu))
            pcuList.add(pcu)
    }

    override fun findByUuid(uuid: UUID): Pcu {
        return pcuList.find { it.uuid == uuid }!!
    }

    override fun findByIpAddress(ipAddress: String): Pcu {
        return pcuList.find { it.lastKnownIp == ipAddress }!!
    }

    override fun remove(pcu: Pcu) {
        pcuList.remove(pcu)
    }

    override fun find(): List<Pcu> {
        return pcuList.toList()
    }
}

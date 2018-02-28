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
import org.junit.Test
import java.util.*

class InMemoryPcuDaoTest {

    private val dao = InMemoryPcuDao.instance

    @Test
    fun findPcuByUuid() {
        val uuid = UUID.randomUUID()
        val pcu = Pcu(uuid)
        dao.insert(pcu)

        val findPcu = dao.findByUuid(uuid)

        assert(findPcu == pcu)
    }

    @Test
    fun findByIp() {
        val pcu = Pcu().apply { lastKnownIp = "127.0.0.1" }
        dao.insert(pcu)

        val findPcu = dao.findByIpAddress("127.0.0.1")

        assert(findPcu == pcu)
    }

}

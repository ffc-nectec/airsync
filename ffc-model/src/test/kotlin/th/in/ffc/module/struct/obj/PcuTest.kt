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

package th.`in`.ffc.module.struct.obj

import org.junit.Test
import java.util.*

class PcuTest {

    @Test
    fun testEqual() {
        val uuid = UUID.randomUUID()
        val pcu1 = Pcu(uuid)
        val pcu2 = Pcu(uuid, "100153", "Nectec41")

        assert(pcu1 == pcu2)
    }

    @Test
    fun testNotEqual() {
        val pcu1 = Pcu(UUID.randomUUID(), "100154", "Nectec41")
        val pcu2 = Pcu(UUID.randomUUID(), "100154", "Nectec41")

        assert(pcu1 != pcu2)
    }
}

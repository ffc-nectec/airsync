/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package ffc.airsync.api.sync

import org.amshove.kluent.`should equal`
import org.junit.Test

class SetUtilTest {

    val list1 = listOf("1", "2", "3", "4", "5")
    val list2 = listOf("2", "3", "5", "6", "7")

    @Test
    fun intersection() {
        val result = SetUtil<String>().intersection(list1, list2) {
            object : SetUtil.Func<String> {
                override val identity: String = it
            }
        }

        result `should equal` listOf("2" to "2", "3" to "3", "5" to "5")
    }

    @Test
    fun difference() {
        val result = SetUtil<String>().difference(list1, list2) {
            object : SetUtil.Func<String> {
                override val identity: String = it
            }
        }
        result `should equal` listOf("1", "4")
    }
}

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

package ffc.airsync.visit

import ffc.airsync.JhcisDBds
import ffc.airsync.MySqlJdbi
import org.junit.Ignore
import org.junit.Test

@Ignore("สำหรับทดสอบฐานจริง")
class NewVisitQueryTest {
    private val jdbi = NewVisitQuery(MySqlJdbi(JhcisDBds().get()))

    @Test
    fun get() {
        val result = jdbi.get("") {
            object : NewVisitQuery.Lookup {
                override fun patientId(pid: String): String = pid
                override fun providerId(username: String): String = username
            }
        }
        println(result.size)
    }
}

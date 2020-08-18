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

package ffc.airsync.db

import org.amshove.kluent.`should equal`
import org.junit.Test

class utilTest {
    private fun String.getSystolic(): Double? =
        Regex("""(\d+)/\d+""").matchEntire(this)?.groupValues?.last()?.toDouble()

    private fun String.getDiastolic(): Double? =
        Regex("""\d+/(\d+)""").matchEntire(this)?.groupValues?.last()?.toDouble()

    val p = "118/72"

    @Test
    fun systolic() {
        p.getSystolic() `should equal` 118.0
    }

    @Test
    fun diastolic() {
        p.getDiastolic() `should equal` 72.0
    }
}

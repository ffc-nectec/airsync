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

package ffc.airsync.api.genogram

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.junit.Test

class FFCAdapterPersonDetailInterfaceTest {

    @Test
    fun `getFirstAndLastName$airsync_main case 1`() {
        val funcTst = FFCAdapterPersonDetailInterface(listOf())
        val name1 = "สมใจ ชื่อนดี"

        val test = funcTst.getFirstAndLastName(name1)
        test.first!! `should be equal to` "สมใจ"
        test.second!! `should be equal to` "ชื่อนดี"
    }

    @Test
    fun `getFirstAndLastName$airsync_main case 2`() {
        val funcTst = FFCAdapterPersonDetailInterface(listOf())
        val name1 = "สมใจ"

        val test = funcTst.getFirstAndLastName(name1)
        test.first!! `should be equal to` "สมใจ"
        test.second `should be` null
    }

    @Test
    fun `getFirstAndLastName$airsync_main case 3`() {
        val funcTst = FFCAdapterPersonDetailInterface(listOf())
        val name1 = "สมใจ "

        val test = funcTst.getFirstAndLastName(name1)
        test.first!! `should be equal to` "สมใจ"
        test.second `should be` null
    }

    @Test
    fun `getFirstAndLastName$airsync_main case 4`() {
        val funcTst = FFCAdapterPersonDetailInterface(listOf())
        val name1 = ""

        val test = funcTst.getFirstAndLastName(name1)
        test.first `should be` null
        test.second `should be` null
    }

    @Test
    fun `getFirstAndLastName$airsync_main case 5`() {
        val funcTst = FFCAdapterPersonDetailInterface(listOf())
        val name1 = " "

        val test = funcTst.getFirstAndLastName(name1)
        test.first `should be` null
        test.second `should be` null
    }

    @Test
    fun `getFirstAndLastName$airsync_main case 6`() {
        val funcTst = FFCAdapterPersonDetailInterface(listOf())
        val name1 = "ปรีชม  มาดี"

        val test = funcTst.getFirstAndLastName(name1)
        test.first!! `should be equal to` "ปรีชม"
        test.second!! `should be equal to` "มาดี"
    }
}

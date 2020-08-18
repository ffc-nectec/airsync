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

package ffc.airsync.utils

import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test

class UploadSpliterTest {
    val list = ArrayList<String>()

    @Before
    fun setUp() {
        for (i in 1..100) {
            list.add("$i")
        }
    }

    @Test
    fun uploadBestCase() {
        val array = arrayListOf<List<String>>()
        UploadSpliter.upload(10, list) { it, index ->
            array.add(it)
        }

        array.size `should be equal to` 10
        array.first().longStr() `should be equal to` "1 2 3 4 5 6 7 8 9 10"
        array.last().longStr() `should be equal to` "91 92 93 94 95 96 97 98 99 100"
    }

    @Test
    fun uploadScraps() {
        val array = arrayListOf<List<String>>()
        UploadSpliter.upload(7, list) { it, index ->
            array.add(it)
        }

        array.size `should be equal to` 15
        array.first().longStr() `should be equal to` "1 2 3 4 5 6 7"
        array[array.size - 2].longStr() `should be equal to` "92 93 94 95 96 97 98"
        array.last().longStr() `should be equal to` "99 100"
    }

    fun List<String>.longStr(): String {
        var strOut = ""
        forEach {
            strOut += "$it "
        }
        return strOut.trimEnd()
    }
}

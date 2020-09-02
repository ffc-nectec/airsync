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

import ffc.entity.Person
import ffc.entity.util.generateTempId
import org.amshove.kluent.shouldContainAll
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class ListToFileKtTest {

    val directory = File("src/test/resources/testsave")

    @Test
    fun saveResource() {
        val list = arrayListOf<Person>()

        list.add(Person(generateTempId(), "สม", "หมาย"))
        list.add(Person(generateTempId(), "ใจ", "ดี"))
        ffcFileSave(File(directory, "test.json"), list)
        val load = ffcFileLoad<Person>(File(directory, "test.json"))

        list shouldContainAll load
    }

    @Before
    fun setUp() {
        directory.mkdir()
    }

    @After
    fun tearDown() {
        directory.deleteOnExit()
    }
}

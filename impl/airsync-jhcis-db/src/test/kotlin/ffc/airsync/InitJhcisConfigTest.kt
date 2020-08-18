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

package ffc.airsync

import mysql.config.copyFileUsingStream
import mysql.config.read.ReadOptionMyini
import org.amshove.kluent.`should be equal to`
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import java.io.File

class InitJhcisConfigTest {
    private val mySmall = File("src/test/resources/my-small.ini")
    private val fileTest = File("mmyy.ini")

    private lateinit var mangeConfig: InitJhcisConfig

    @Before
    fun setUp() {
        copyFileUsingStream(mySmall, fileTest)
        mangeConfig = InitJhcisConfig(fileTest, "5.0.51b-community-nt-log")
    }

    @After
    fun tearDown() {
        fileTest.deleteOnExit()
    }

    @Ignore("Require admin")
    fun `checkConfig$airsync`() {
        mangeConfig.writeOldMySqlConfig() `should be equal to` true

        ReadOptionMyini(fileTest).read().getValue("mysqld").getValue("log") `should be equal to` "jlog.log"
    }
}

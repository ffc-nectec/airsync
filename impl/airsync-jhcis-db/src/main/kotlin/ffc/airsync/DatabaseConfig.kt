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

import java.io.File
import java.io.FileInputStream
import java.util.Properties

internal class DatabaseConfig {
    val jhcisConfigFile = "C:\\Program Files\\JHCIS\\database.properties"
    private val property = Properties()

    init {
        val file = File(jhcisConfigFile)
        require(file.isFile) { "ไม่พบไฟล์ $jhcisConfigFile ของตัวระบบ JHCIS" }
        property.load(FileInputStream(file))
    }

    val server get() = property.getProperty("SERVER")
    val port get() = property.getProperty("PORT")
    val username get() = property.getProperty("USERNAME")
    val password get() = property.getProperty("PASSWORD")
    val databaseName get() = property.getProperty("DATABASE")
    val currentOrganization get() = property.getProperty("PCUCODE")
}

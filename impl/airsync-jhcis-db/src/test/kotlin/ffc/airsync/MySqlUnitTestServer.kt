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

import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.ScriptResolver
import com.wix.mysql.config.Charset.UTF8
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version.v5_6_23
import java.sql.Connection
import java.sql.DriverManager
import java.util.concurrent.TimeUnit

class MySqlUnitTestServer(
    private val databaseName: String,
    private val initSqlResource: String,
    private val port: Int = mySqlPort,
    private val user: String = mySqlUser,
    private val password: String = mySqlPass
) {
    val connection: Connection
    private val config = aMysqldConfig(v5_6_23)
        .withCharset(UTF8)
        .withPort(port)
        .withUser(user, password)
        .withTimeZone("Europe/Vilnius")
        .withTimeout(2, TimeUnit.MINUTES)
        .withServerVariable("max_connect_errors", 666)
        .build()

    private val mysqld: EmbeddedMysql = anEmbeddedMysql(config)
        .addSchema(databaseName, ScriptResolver.classPathScript(initSqlResource))
        .start()

    init {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:$port/$databaseName", user, password)
    }

    fun stop() {
        mysqld.stop()
    }
}

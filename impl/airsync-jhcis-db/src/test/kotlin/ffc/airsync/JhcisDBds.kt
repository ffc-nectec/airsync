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

import javax.sql.DataSource

class JhcisDBds {

    fun get(): DataSource {
        Class.forName("com.mysql.jdbc.Driver")

        val dsMySql = com.mysql.jdbc.jdbc2.optional.MysqlDataSource()

        val dbHost = "127.0.0.1"
        val dbPort = "3333"
        val dbName = "jhcisdb"
        val dbUsername = "root"
        val dbPassword = ""

        dsMySql.setURL(
            "jdbc:mysql://$dbHost:$dbPort/$dbName?" +
                    "autoReconnect=true&" +
                    "useSSL=false&" +
                    "maxReconnects=2&" +
                    "autoReconnectForPools=true&" +
                    "connectTimeout=10000&" +
                    "socketTimeout=10000"
        )
        dsMySql.databaseName = dbName
        dsMySql.user = dbUsername
        dsMySql.setPassword(dbPassword)
        dsMySql.port = dbPort.toInt()
        return dsMySql
    }
}

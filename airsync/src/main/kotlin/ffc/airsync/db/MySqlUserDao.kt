/*
 * Copyright (c) 2561 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.airsync.db

import ffc.airsync.utils.printDebug
import ffc.entity.User
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

const val MYSQL_DRIVER = "com.mysql.jdbc.Driver"

class MySqlUserDao : UserDao {
    override fun findAll(): ArrayList<User> {

        var conn: Connection? = null
        val userList = arrayListOf<User>()

        try {
            Class.forName(MYSQL_DRIVER)
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3333/jhcisdb"
                    + "?autoReconnect=true&useSSL=false", "root", "123456")

            if (conn != null) {
                printDebug("Database Connected.")

                var query = "SELECT * FROM `jhcisdb`.`user` LIMIT 1000"
                val st = conn.createStatement()
                val rs = st.executeQuery(query)

                while (rs.next()) {
                    val username = rs.getString("username")
                    val password = rs.getString("password")
                    userList.add(User(username).apply {
                        this.password = password
                    })
                    printDebug("User = " + username + " Pass= " + password)
                }
            } else {
                printDebug("Database Connect Failed.")
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }
        try {
            conn?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return userList
    }
}

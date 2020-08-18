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

import ffc.airsync.JdbiDao
import ffc.airsync.MySqlJdbi
import ffc.airsync.db.DatabaseDao
import org.h2.jdbcx.JdbcConnectionPool
import java.sql.DriverManager

fun JdbiDao.createTest(): DatabaseDao {
    val jdbUrlString = "jdbc:h2:mem:test"
    val dbUser = "root"
    val dbPassword = "123456"

    val ds = JdbcConnectionPool.create(
        "$jdbUrlString;MODE=MySQL",
        dbUser,
        dbPassword
    )

    val conn = DriverManager.getConnection(jdbUrlString, dbUser, dbPassword)

    conn.createStatement().executeQuery("create database jhcisdb")

    return JdbiDao(MySqlJdbi(ds))
}

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

package ffc.airsync.user

import ch.vorburger.mariadb4j.DB
import ffc.entity.User
import org.junit.Ignore
import org.junit.Test

/**
 * สำหรับทดสอบกับฐาน mySql
 */
@Ignore("สำหรับทดสอบ")
class UserJdbiTest {
    // val dao = UserJdbi(MySqlJdbi(JhcisDBds().get()))

    @Test
    fun get() {
        /*val data = dao.get()
        data.forEach { user ->
            println("${user.getIdCard()}")
        }
        println(data)*/
    }

    @Test
    fun startDb() {
        val database = DB.newEmbeddedDB(34323)
        println("create")
        // database.start()
    }
}

internal fun User.getIdCard(): String? {
    return this.link?.keys?.get("idcard")?.toString()
}

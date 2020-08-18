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

import ffc.airsync.Dao
import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.airsync.getLogger
import ffc.entity.User

class UserJdbi(
    val jdbiDao: Dao = MySqlJdbi(null)
) : UserDao {
    private val logger = getLogger(this)
    override fun get(): List<User> {
        return jdbiDao.extension<QueryUser, List<User>> { get() }.filter {
            kotlin.runCatching {
                val test = "${it.name}:" + it.password
            }.isSuccess
        }
    }
}

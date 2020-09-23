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

package ffc.airsync.api.user

import ffc.airsync.Main
import ffc.airsync.api.user.sync.UpdateAndCreateList
import ffc.airsync.db.DatabaseDao
import ffc.airsync.utils.getLogger
import ffc.entity.Entity
import ffc.entity.User
import ffc.entity.copy
import ffc.entity.gson.toJson
import kotlinx.coroutines.runBlocking

class UserManage(
    val dao: DatabaseDao = Main.instant.dao,
    val userApi: UserApi = UserServiceApi()
) : UserInterface {
    private var cloudCache = listOf<User>()
    private val logger = getLogger(this)
    override val localUser: List<User> get() = dao.getUsers()

    override val cloudUser: List<User>
        get() {
            if (cloudCache.isEmpty())
                cloudCache = userApi.get().copy()
            return cloudCache
        }

    override fun sync(forceUpdate: Boolean): List<Entity> {
        logger.info { "Sync user forceUpdate:$forceUpdate" }
        if (forceUpdate) runBlocking {
            val local = localUser
            val cloud = userApi.get()
            val (_, _, all) = UpdateAndCreateList().getList(local, cloud)
            val allData = all.filter { it.name.trim().isNotEmpty() }
            allData.forEach {
                logger.debug { "Force update user ${it.name}" }
                userApi.update(listOf(it))
            }
        }
        else runBlocking {
            val local = localUser
            val cloud = userApi.get()
            val (update, create, all) = UpdateAndCreateList().getList(local, cloud)
            val updateData = update.filter { it.name.trim().isNotEmpty() }
            val createData = create.filter { it.name.trim().isNotEmpty() }

            if (updateData.isNotEmpty())
                updateData.forEach {
                    logger.debug { "Update user ${it.name}" }
                    userApi.update(listOf(it))
                }
            if (createData.isNotEmpty()) {
                logger.debug { "Create user Size:${createData.size} name:${createData.map { it.name }.toJson()}" }
                userApi.create(createData)
            }
        }
        logger.debug { "Get user form api to cloudCache" }
        cloudCache = userApi.get().copy()
        return cloudCache.copy()
    }

    private fun List<User>.copy(): List<User> = map { it.copy() }
}

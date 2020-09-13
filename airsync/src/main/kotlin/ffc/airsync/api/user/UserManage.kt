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
import ffc.entity.Entity
import ffc.entity.User
import ffc.entity.copy
import kotlinx.coroutines.runBlocking

class UserManage(
    val dao: DatabaseDao = Main.instant.dao,
    val userApi: UserApi = UserServiceApi()
) : UserInterface {
    private var cloudCache = listOf<User>()
    override val localUser: List<User> get() = dao.getUsers()

    override val cloudUser: List<User>
        get() {
            if (cloudCache.isEmpty())
                cloudCache = userApi.get().copy()
            return cloudCache
        }

    override fun sync(forceUpdate: Boolean): List<Entity> {
        runBlocking {
            val local = localUser
            val cloud = userApi.get()
            val (update, create, all) = UpdateAndCreateList().getList(local, cloud)

            if (update.isNotEmpty())
                userApi.update(update)
            if (create.isNotEmpty())
                userApi.create(create)
        }

        if (forceUpdate) runBlocking {
            val local = localUser
            val cloud = userApi.get()
            val (_, _, all) = UpdateAndCreateList().getList(local, cloud)
            userApi.update(all)
        }

        cloudCache = userApi.get().copy()
        return cloudCache.copy()
    }

    private fun List<User>.copy(): List<User> = map { it.copy() }
}

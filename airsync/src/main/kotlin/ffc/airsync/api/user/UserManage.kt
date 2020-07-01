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

    override fun sync(force: Boolean): List<Entity> {
        runBlocking {
            val local = localUser
            val cloud = userApi.get()
            val (update, create, all) = UpdateAndCreateList().getList(local, cloud)

            if (update.isNotEmpty())
                userApi.update(update)
            if (create.isNotEmpty())
                userApi.create(create)
        }

        if (force) runBlocking {
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

package ffc.airsync.api.user

import ffc.airsync.Main
import ffc.airsync.db.DatabaseDao
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.User

fun User.gets(dao: DatabaseDao = Main.instant.createDatabaseDao()): List<User> {
    return dao.getUsers()
}

fun ArrayList<User>.initSync() {
    val localUser = arrayListOf<User>().apply {
        addAll(load())
    }
    if (localUser.isEmpty()) {
        localUser.addAll(User().gets())
        addAll(userApi.putUser(localUser.toMutableList()))
        save()
    } else {
        addAll(localUser)
    }
}

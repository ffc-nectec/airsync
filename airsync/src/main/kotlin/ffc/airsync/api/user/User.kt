package ffc.airsync.api.user

import ffc.airsync.Main
import ffc.airsync.db.DatabaseDao
import ffc.entity.User

fun User.gets(dao: DatabaseDao = Main.instant.createDatabaseDao()): List<User> {
    return dao.getUsers()
}

package ffc.airsync.api.user

import ffc.airsync.Main
import ffc.airsync.db.DatabaseDao
import ffc.airsync.userApi
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.User
import ffc.entity.gson.toJson

fun User.gets(dao: DatabaseDao = Main.instant.dao): List<User> {
    return dao.getUsers()
}

fun ArrayList<User>.initSync() {
    val localUser = arrayListOf<User>()
    localUser.addAll(localUser.load())

    if (localUser.isEmpty()) {
        localUser.addAll(User().gets())
        check(localUser.isNotEmpty()) {
            "เกิดข้อผิดพลาด " +
                    "ในการดึงข้อมูลการ Login ใน table user " +
                    "ไม่สามารถ ใส่ข้อมูล User ได้"
        }
        val putUser = userApi.putUser(localUser.toMutableList())
        addAll(putUser)
        check(localUser.isNotEmpty()) {
            "เกิดข้อผิดพลาด " +
                    "ในการ Sync User จาก Cloud"
        }
        save()
    } else {
        checkAndUpdateNewUser(localUser)
        addAll(localUser)
    }
}

private fun ArrayList<User>.checkAndUpdateNewUser(localUser: ArrayList<User>) {
    val userFromCloud = userApi.getuser()
    val newUser = arrayListOf<User>()

    localUser.forEach { local ->
        val cloud = userFromCloud.findUser(local.name)
        if (cloud == null) {
            newUser.add(local)
        }
    }

    if (newUser.isNotEmpty()) {
        getLogger(this).info { "Update new user ${newUser.toJson()}" }
        val putUser = userApi.putUser(newUser.toMutableList())
        localUser.addAll(putUser)
        save()
    }
}

private fun List<User>.findUser(name: String): User? = find { it.name == name }

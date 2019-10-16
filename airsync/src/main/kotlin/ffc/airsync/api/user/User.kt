package ffc.airsync.api.user

import ffc.airsync.Main
import ffc.airsync.db.DatabaseDao
import ffc.airsync.userApi
import ffc.airsync.utils.checkDataUpdate
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.User
import ffc.entity.gson.toJson

/**
 * ดึงข้อมูล User ทั้งหมดจาก jhcisdb
 */
fun User.gets(dao: DatabaseDao = Main.instant.dao): List<User> {
    return dao.getUsers()
}

fun ArrayList<User>.initSync() {
    val localUser = arrayListOf<User>()
    localUser.addAll(localUser.load())

    val jhcisUser = User().gets()
    if (localUser.isEmpty()) {
        localUser.addAll(jhcisUser)
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
        val cloudUser = userApi.getuser()
        checkDataUpdate(jhcisUser, cloudUser, { jhcis, cloud -> jhcis.name == cloud.name }) {
            getLogger(this).info { "Update new user ${it.toJson()}" }
            val putUser = userApi.putUser(it.toMutableList())
            localUser.addAll(putUser)
            save()
        }
        addAll(localUser)
    }
}



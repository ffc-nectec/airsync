package ffc.airsync.api.user

import ffc.airsync.Main
import ffc.airsync.db.DatabaseDao
import ffc.airsync.userApi
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.User

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
        addAll(localUser)
    }
}

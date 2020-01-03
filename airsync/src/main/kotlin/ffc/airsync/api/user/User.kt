package ffc.airsync.api.user

import ffc.airsync.Main
import ffc.airsync.db.DatabaseDao
import ffc.airsync.userApi
import ffc.airsync.users
import ffc.airsync.utils.checkNewDataCreate
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.User

/**
 * ดึงข้อมูล User ทั้งหมดจาก jhcisdb
 */
fun User.gets(dao: DatabaseDao = Main.instant.dao): List<User> {
    return dao.getUsers()
}

@Deprecated("ถ้า syncUser OK แล้วจะเปลี่ยนไปใช้", ReplaceWith("syncUser()", "ffc.airsync.api.user.syncUser"))
fun ArrayList<User>.initSync() {
    this.lock {
        val cacheFile = arrayListOf<User>()
        cacheFile.addAll(load())

        val jhcisUser = User().gets()
        clear()
        if (cacheFile.isEmpty()) {
            check(jhcisUser.isNotEmpty()) {
                "เกิดข้อผิดพลาด " +
                        "ในการดึงข้อมูลการ Login ใน table user " +
                        "ไม่สามารถ ใส่ข้อมูล User ได้"
            }
            val putUser = userApi.register(jhcisUser.toMutableList())
            addAll(putUser)
            check(isNotEmpty()) {
                "เกิดข้อผิดพลาด " +
                        "ในการ Sync User จาก Cloud"
            }
            save()
        } else {
            addAll(cacheFile)
            checkNewDataCreate(jhcisUser, cacheFile, { jhcis, cloud -> jhcis.name == cloud.name }) {
                create(it)
            }
        }
    }
}

/**
 * ทำการ sync user จาก local ไปยัง cloud
 * โดย cloud จะทำงานเรื่องการ ตรวจสอบให้อัตโนมัติ
 * -- รายการตรวจสอบอัตโนมัติ --
 * - user ที่มีบน local แต่ไม่มีบน cloud ให้เพิ่มใหม่
 * - user ที่มีบน local และมีบน cloud ให้ update ข้อมูล
 * - user ที่มีบน cloud แต่ไม่มีบน local ให้ลบออก
 */
fun syncUser() {
    val jhcisUser = User().gets()
    if (jhcisUser.isNotEmpty())
        jhcisUser.lock {
            val sync = userApi.sync(jhcisUser)
            if (sync.isNotEmpty())
                sync.save()
            else
                getLogger(sync).warn("User.kt sync user function is empty.")
        }
}

fun ArrayList<User>.syncJToCloud() {
    val jhcisUser = User().gets()
    checkNewDataCreate(jhcisUser, this, { jhcis, cloud -> jhcis.name == cloud.name }) {
        create(it)
    }
}

private fun ArrayList<User>.create(it: List<User>) {
    getLogger(this).info { "Update new user ${it.map { it.name }}" }
    val putUser = userApi.register(it.toMutableList())
    addAll(putUser)
    save()
}

private const val userLock = "lock"

fun List<User>.lock(f: () -> Unit) {
    synchronized(userLock) {
        f()
    }
}

fun findProviderId(name: String): String {
    val id = (users.find { it.name == name })?.id
    return if (id == null) {
        syncUser()
        (users.find { it.name == name })!!.id
    } else
        id
}

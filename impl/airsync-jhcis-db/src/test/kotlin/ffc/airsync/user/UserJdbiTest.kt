package ffc.airsync.user

import ch.vorburger.mariadb4j.DB
import ffc.entity.User
import org.junit.Ignore
import org.junit.Test

/**
 * สำหรับทดสอบกับฐาน mySql
 */
@Ignore("สำหรับทดสอบ")
class UserJdbiTest {
    // val dao = UserJdbi(MySqlJdbi(JhcisDBds().get()))

    @Test
    fun get() {
        /*val data = dao.get()
        data.forEach { user ->
            println("${user.getIdCard()}")
        }
        println(data)*/
    }

    @Test
    fun startDb() {
        val database = DB.newEmbeddedDB(34323)
        println("create")
        // database.start()
    }
}

internal fun User.getIdCard(): String? {
    return this.link?.keys?.get("idcard")?.toString()
}

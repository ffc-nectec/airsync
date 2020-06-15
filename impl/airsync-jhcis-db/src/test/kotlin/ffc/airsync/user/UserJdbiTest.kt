package ffc.airsync.user

import ffc.airsync.JhcisDBds
import ffc.airsync.MySqlJdbi
import org.junit.Ignore
import org.junit.Test

/**
 * สำหรับทดสอบกับฐาน mySql
 */
@Ignore("สำหรับทดสอบ")
class UserJdbiTest {
    val dao = UserJdbi(MySqlJdbi(JhcisDBds().get()))

    @Test
    fun get() {
        val data = dao.get()
        println(data)
    }
}

package ffc.airsync.hosdetail

import ffc.airsync.JhcisDBds
import ffc.airsync.MySqlJdbi
import org.junit.Ignore
import org.junit.Test

/**
 * สำหรับทดสอบกับฐาน mySql
 */
@Ignore("สำหรับทดสอบ")
class HosDetailJdbiTest {
    val dao = HosDetailJdbi(MySqlJdbi(JhcisDBds().get()))

    @Test
    fun get() {
        val data = dao.get()
        println(data)
    }
}

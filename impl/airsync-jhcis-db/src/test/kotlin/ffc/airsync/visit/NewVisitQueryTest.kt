package ffc.airsync.visit

import ffc.airsync.JhcisDBds
import ffc.airsync.MySqlJdbi
import org.junit.Ignore
import org.junit.Test

@Ignore("สำหรับทดสอบฐานจริง")
class NewVisitQueryTest {
    private val jdbi = NewVisitQuery(MySqlJdbi(JhcisDBds().get()))

    @Test
    fun get() {
        val result = jdbi.get("") {
            object : NewVisitQuery.Lookup {
                override fun patientId(pid: String): String = pid
                override fun providerId(username: String): String = username
            }
        }
        println(result.size)
    }
}

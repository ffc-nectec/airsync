package ffc.airsync.db

import org.amshove.kluent.`should equal`
import org.junit.Test

class utilTest {
    private fun String.getSystolic(): Double? =
        Regex("""(\d+)/\d+""").matchEntire(this)?.groupValues?.last()?.toDouble()

    private fun String.getDiastolic(): Double? =
        Regex("""\d+/(\d+)""").matchEntire(this)?.groupValues?.last()?.toDouble()

    val p = "118/72"

    @Test
    fun systolic() {
        p.getSystolic() `should equal` 118.0
    }

    @Test
    fun diastolic() {
        p.getDiastolic() `should equal` 72.0
    }
}

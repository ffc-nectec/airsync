package ffc.airsync.api.genogram

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.junit.Test

class FFCAdapterPersonDetailInterfaceTest {

    @Test
    fun `getFirstAndLastName$airsync_main case 1`() {
        val funcTst = FFCAdapterPersonDetailInterface(listOf())
        val name1 = "สมใจ ชื่อนดี"

        val test = funcTst.getFirstAndLastName(name1)
        test.first!! `should be equal to` "สมใจ"
        test.second!! `should be equal to` "ชื่อนดี"
    }

    @Test
    fun `getFirstAndLastName$airsync_main case 2`() {
        val funcTst = FFCAdapterPersonDetailInterface(listOf())
        val name1 = "สมใจ"

        val test = funcTst.getFirstAndLastName(name1)
        test.first!! `should be equal to` "สมใจ"
        test.second `should be` null
    }

    @Test
    fun `getFirstAndLastName$airsync_main case 3`() {
        val funcTst = FFCAdapterPersonDetailInterface(listOf())
        val name1 = "สมใจ "

        val test = funcTst.getFirstAndLastName(name1)
        test.first!! `should be equal to` "สมใจ"
        test.second `should be` null
    }

    @Test
    fun `getFirstAndLastName$airsync_main case 4`() {
        val funcTst = FFCAdapterPersonDetailInterface(listOf())
        val name1 = ""

        val test = funcTst.getFirstAndLastName(name1)
        test.first `should be` null
        test.second `should be` null
    }

    @Test
    fun `getFirstAndLastName$airsync_main case 5`() {
        val funcTst = FFCAdapterPersonDetailInterface(listOf())
        val name1 = " "

        val test = funcTst.getFirstAndLastName(name1)
        test.first `should be` null
        test.second `should be` null
    }
}

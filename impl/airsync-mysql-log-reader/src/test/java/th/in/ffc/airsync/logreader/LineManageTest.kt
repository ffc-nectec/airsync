package th.`in`.ffc.airsync.logreader

import org.amshove.kluent.`should be equal to`
import org.junit.Test

class LineManageTest {
    val lineManage = LineManage("lineManageUnitTest.cfg")

    @Test
    fun setAndGetLineNumberProperty() {
        lineManage.setLastLineNumber(250)
        lineManage.getLastLineNumber() `should be equal to` 250
    }

    @Test
    fun setAndGetOthter() {
        lineManage.setProperty("one", "1")
        lineManage.setProperty("two", "2")
        lineManage.getProperty("one") `should be equal to` "1"
        lineManage.getProperty("two") `should be equal to` "2"
    }
}

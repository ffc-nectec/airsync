package ffc.airsync.gui

import ffc.airsync.BuildConfig
import ffc.airsync.provider.createArisyncGui
import ffc.airsync.ui.createCountDownMessage
import ffc.airsync.ui.createMessage
import ffc.airsync.ui.createProgress
import ffc.airsync.utils.toBuddistString
import org.joda.time.DateTime
import org.junit.Ignore
import org.junit.Test

@Ignore("Example")
class AirSyncGUITest {
    @Test
    fun gui() {
        val gui = createArisyncGui()
        gui.setHeader(BuildConfig.VERSION)
        gui.showWIndows()
        Thread.sleep(1000)
        gui.createProgress("Person", 20, 100)
        Thread.sleep(2000)
        gui.createProgress("Person", 30, 100, "Ack")
        gui.createProgress("House", 90, 100, "Ackr")
        Thread.sleep(2000)
        gui.remove("Person")
        gui.remove("House")
        gui.enableSyncButton = true
        gui.createMessage("Person", "Person \r\nSuccess")
        Thread.sleep(1000)
        println("Create countdown")
        gui.createCountDownMessage("count", "123 432", 60)
        live()
    }

    private fun live() {
        while (true)
            Thread.sleep(10000)
    }

    @Test
    fun showOtp() {
        val gui = createArisyncGui()
        gui.setHeader(BuildConfig.VERSION)
        gui.showWIndows()
        gui.createMessage("Success", "ข้อมูล Sync เข้าระบบสำเร็จแล้ว\r\nล่าสุด ${DateTime.now().toBuddistString()}")
        gui.enableOtp = true
        gui.callGetOtp = { "123 432" }
        live()
    }
}

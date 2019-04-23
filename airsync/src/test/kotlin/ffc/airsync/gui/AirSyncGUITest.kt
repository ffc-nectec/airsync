package ffc.airsync.gui

import ffc.airsync.BuildConfig
import ffc.airsync.provider.createArisyncGui
import ffc.airsync.ui.createCountDownMessage
import ffc.airsync.ui.createMessage
import ffc.airsync.ui.createProgress
import org.junit.Ignore

class AirSyncGUITest {
    @Ignore("Example")
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
        gui.createCountDownMessage("count", "123 432", 50)
        while (true)
            Thread.sleep(10000)
    }
}

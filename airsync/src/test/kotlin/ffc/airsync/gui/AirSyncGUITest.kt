package ffc.airsync.gui

import ffc.airsync.BuildConfig
import ffc.airsync.provider.createArisyncGui
import ffc.airsync.ui.AirSyncGUI
import org.junit.Ignore

class AirSyncGUITest {
    @Ignore("Example")
    fun gui() {
        val gui = createArisyncGui()
        gui.setHeader(BuildConfig.VERSION)
        gui.showWIndows()
        Thread.sleep(1000)
        gui.set("Person" to AirSyncGUI.ProgressData(20, 100))
        Thread.sleep(2000)
        gui.set("Person" to AirSyncGUI.ProgressData(30, 100, "Ack"))
        gui.set("House" to AirSyncGUI.ProgressData(90, 100, "Ackr"))
        Thread.sleep(2000)
        gui.remove("Person")
        gui.remove("House")
        gui.enableSyncButton = true
        gui.set("Person" to AirSyncGUI.CheckData("Person \r\nSuccess"))
        while (true)
            Thread.sleep(10000)
    }
}

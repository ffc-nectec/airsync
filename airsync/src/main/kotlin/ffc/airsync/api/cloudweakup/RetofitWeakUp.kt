package ffc.airsync.api.cloudweakup

import ffc.airsync.Config
import ffc.airsync.gui
import ffc.airsync.printDebug
import ffc.airsync.retrofit.ApiFactory
import ffc.airsync.ui.AirSyncGUI
import java.net.SocketTimeoutException

class RetofitWeakUp : WeakUpApi {

    private val restService = ApiFactory().buildApiClient(Config.baseUrlRest, WeakUpUrl::class.java, 128)
    override fun weakUp() {
        var count = 1
        val limitCount = 5
        var cloudStatusDown = true
        while (cloudStatusDown && count++ <= limitCount) {
            try {
                printDebug("Wake cloud loop ${count - 1} in $limitCount")
                val response = restService.checkCloud().execute()
                gui.remove("Cloud Network error")
                if (response.code() == 200)
                    cloudStatusDown = false
            } catch (ignore: SocketTimeoutException) {
                cloudStatusDown = true
                Thread.sleep(3000)
            } catch (ex: java.net.UnknownHostException) {
                gui.set(
                    "Cloud Network error" to AirSyncGUI.Message(
                        "Network Error $ex",
                        AirSyncGUI.MESSAGE_TYPE.ERROR
                    )
                )
                Thread.sleep(3000)
            }
        }
    }
}

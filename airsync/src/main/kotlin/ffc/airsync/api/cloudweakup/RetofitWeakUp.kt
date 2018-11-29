package ffc.airsync.api.cloudweakup

import ffc.airsync.Config
import ffc.airsync.retrofit.ApiFactory
import ffc.airsync.utils.printDebug
import java.net.SocketTimeoutException

class RetofitWeakUp : WeakUpApi {

    private val restService = ApiFactory().buildApiClient(Config.baseUrlRest, WeakUpUrl::class.java, 0)
    override fun weakUp() {
        var count = 1
        val limitCount = 5
        var cloudStatusDown = true
        while (cloudStatusDown && count++ <= limitCount) {
            try {
                printDebug("Wake cloud loop ${count - 1} in $limitCount")
                val response = restService.checkCloud().execute()
                if (response.code() == 200)
                    cloudStatusDown = false
            } catch (ignore: SocketTimeoutException) {
                cloudStatusDown = true
                Thread.sleep(3000)
            }
        }
    }
}

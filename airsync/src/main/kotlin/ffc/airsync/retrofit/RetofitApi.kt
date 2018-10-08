package ffc.airsync.retrofit

import ffc.airsync.utils.printDebug
import ffc.entity.Organization
import ffc.entity.Token
import java.net.SocketTimeoutException

abstract class RetofitApi(val organization: Organization, urlBase: String, val token: Token) {
    val tokenBarer: String
        get() = "Bearer " + token.token
    val pcucode: String
        get() = (organization.link!!.keys["pcucode"] as String).trim()
    val restService = ApiFactory().buildApiClient(urlBase)

    protected fun wakeCloud() {
        var count = 1
        val limitCount = 5
        var cloudStatusDown = true
        while (cloudStatusDown && count++ <= limitCount) {
            try {
                printDebug("Wake cloud loop ${count - 1} in $limitCount")
                restService.checkCloud()
                cloudStatusDown = false
            } catch (ignore: SocketTimeoutException) {
                Thread.sleep(3000)
            }
        }
    }
}

package ffc.airsync.retrofit

import ffc.airsync.Config
import ffc.entity.Organization
import ffc.entity.Token

abstract class RetofitApi<T>(
    retofitUrl: Class<T>,
    cacheKbyte: Int = 1024
) {

    companion object {
        lateinit var organization: Organization
        lateinit var token: Token
    }

    val urlBase: String
        get() = Config.baseUrlRest
    val tokenBarer: String
        get() = "Bearer " + token.token
    val pcucode: String
        get() = (organization.link!!.keys["pcucode"] as String).trim()
    val restService = ApiFactory().buildApiClient(urlBase, retofitUrl, cacheKbyte)
}

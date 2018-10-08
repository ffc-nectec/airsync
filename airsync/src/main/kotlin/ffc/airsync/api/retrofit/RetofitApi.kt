package ffc.airsync.api.retrofit

import ffc.airsync.api.ApiFactory
import ffc.entity.Organization
import ffc.entity.Token

abstract class RetofitApi(val organization: Organization, urlBase: String, val token: Token) {
    val tokenBarer: String
        get() = "Bearer " + token.token
    val restService = ApiFactory().buildApiClient(urlBase)
}

package ffc.airsync.api.user

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi
import ffc.entity.User

class RetofitUserApi : RetofitApi<UserUrl>(UserUrl::class.java), UserApi {
    override fun putUser(userInfoList: List<User>): List<User> {
        return try {
            val respond =
                restService.regisUser(user = userInfoList, orgId = organization.id, authkey = tokenBarer).execute()
            respond.body() ?: arrayListOf()
        } catch (ex: java.net.SocketTimeoutException) {
            callApi { restService.getUser(organization.id, tokenBarer).execute().body() }
        }
    }
}

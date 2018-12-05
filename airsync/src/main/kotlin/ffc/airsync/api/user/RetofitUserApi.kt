package ffc.airsync.api.user

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi
import ffc.entity.User

class RetofitUserApi : RetofitApi<UserUrl>(UserUrl::class.java), UserApi {
    override fun putUser(userInfoList: List<User>): List<User> {
        return try {
            val respond =
                restService.regisUser(user = userInfoList, orgId = organization.id, authkey = tokenBarer).execute()
            if (respond.code() == 200 || respond.code() == 201)
                respond.body() ?: arrayListOf()
            else {
                getuser()
            }
        } catch (ex: java.net.SocketTimeoutException) {
            getuser()
        }
    }

    private fun getuser(): List<User> {
        Thread.sleep(10000)
        return callApi { restService.getUser(organization.id, tokenBarer).execute().body() }
    }
}

package ffc.airsync.api.user

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi
import ffc.entity.User

class UserServiceApi : RetofitApi<UserService>(UserService::class.java), UserApi {
    override fun register(userInfoList: List<User>): List<User> {
        return try {
            val respond =
                restService.register(user = userInfoList, orgId = organization.id, authkey = tokenBarer).execute()
            if (respond.code() == 200 || respond.code() == 201)
                respond.body() ?: arrayListOf()
            else {
                get()
            }
        } catch (ex: java.net.SocketTimeoutException) {
            Thread.sleep(10000)
            get()
        }
    }

    override fun get(): List<User> {
        return callApi { restService.get(organization.id, tokenBarer).execute().body() }
    }

    override fun sync(userList: List<User>): List<User> {
        return callApi { restService.sync(organization.id, tokenBarer, userList).execute().body() }
    }
}

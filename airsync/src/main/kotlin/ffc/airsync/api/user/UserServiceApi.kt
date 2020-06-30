package ffc.airsync.api.user

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi
import ffc.entity.User

class UserServiceApi : RetofitApi<UserService>(UserService::class.java), UserApi {
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
            Thread.sleep(10000)
            getuser()
        }
    }

    override fun getuser(): List<User> {
        return callApi {
            val execute = restService.getUser(organization.id, tokenBarer).execute()
            when (val status = execute.code()) {
                200 -> execute.body()
                404 -> emptyList()
                else -> throw Exception("Api error $status ${execute.errorBody()}")
            }
        }
    }
}

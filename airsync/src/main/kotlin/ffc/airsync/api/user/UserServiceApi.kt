package ffc.airsync.api.user

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi
import ffc.entity.User

class UserServiceApi : RetofitApi<UserService>(UserService::class.java), UserApi {
    override fun create(userInfoList: List<User>): List<User> {
        return try {
            val respond =
                restService.regisUser(user = userInfoList, orgId = organization.id, authkey = tokenBarer).execute()
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
        return callApi {
            val execute = restService.getUser(organization.id, tokenBarer).execute()
            when (val status = execute.code()) {
                200 -> execute.body()
                404 -> emptyList()
                else -> throw Exception("Api error $status ${execute.errorBody()}")
            }
        }
    }

    override fun update(update: List<User>): List<User> {
        return callApi {
            val execute = restService.updateUser(organization.id, tokenBarer, update).execute()
            when (val status = execute.code()) {
                200 -> execute.body()
                else -> throw Exception("Api error $status ${execute.errorBody()}")
            }
        }
    }
}

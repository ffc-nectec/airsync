/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package ffc.airsync.api.user

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi
import ffc.entity.User
import ffc.entity.copy

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
                else ->
                    throw Exception("Api error $status ${execute.errorBody()?.byteStream()?.reader()?.readText()}")
            }
        }
    }

    override fun update(update: List<User>): List<User> {
        val list = update.map {
            val copy = it.copy()
            copy.link?.keys?.set("password", it.password)
            copy
        }
        return callApi {
            val execute = restService.updateUser(organization.id, tokenBarer, list).execute()
            when (val status = execute.code()) {
                200 -> execute.body()
                else -> throw Exception("Api error $status ${execute.errorBody()?.charStream()?.readText()}")
            }
        }
    }
}

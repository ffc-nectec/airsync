package ffc.airsync.api

import ffc.entity.User

interface UserApi {
    fun putUser(userInfoList: List<User>): List<User>
}

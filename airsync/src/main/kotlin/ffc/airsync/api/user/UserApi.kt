package ffc.airsync.api.user

import ffc.entity.User

interface UserApi {
    fun putUser(userInfoList: List<User>): List<User>
}

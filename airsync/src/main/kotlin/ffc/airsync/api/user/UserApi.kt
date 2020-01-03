package ffc.airsync.api.user

import ffc.entity.User

interface UserApi {
    fun register(userInfoList: List<User>): List<User>
    fun get(): List<User>
    fun sync(userList: List<User>): List<User>
}

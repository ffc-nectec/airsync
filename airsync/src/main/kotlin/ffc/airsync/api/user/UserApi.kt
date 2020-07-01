package ffc.airsync.api.user

import ffc.entity.User

interface UserApi {
    fun create(userInfoList: List<User>): List<User>
    fun get(): List<User>
    fun update(update: List<User>): List<User>
}

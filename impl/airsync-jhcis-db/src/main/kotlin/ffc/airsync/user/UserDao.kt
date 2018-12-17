package ffc.airsync.user

import ffc.entity.User

interface UserDao {
    fun get(): List<User>
}

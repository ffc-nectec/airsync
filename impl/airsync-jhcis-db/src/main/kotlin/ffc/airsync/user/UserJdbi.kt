package ffc.airsync.user

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.User

class UserJdbi(
    val jdbiDao: MySqlJdbi = MySqlJdbi(null)
) : UserDao {
    override fun get(): List<User> {
        return jdbiDao.extension<QueryUser, List<User>> { get() }
    }
}

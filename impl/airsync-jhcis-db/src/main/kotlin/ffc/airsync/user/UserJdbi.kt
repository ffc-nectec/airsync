package ffc.airsync.user

import ffc.airsync.Dao
import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.User

class UserJdbi(
    val jdbiDao: Dao = MySqlJdbi(null)
) : UserDao {
    override fun get(): List<User> {
        return jdbiDao.extension<QueryUser, List<User>> { get() }
    }
}

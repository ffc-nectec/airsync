package ffc.airsync.user

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.User
import javax.sql.DataSource

class UserJdbi(
    ds: DataSource? = null
) : MySqlJdbi(ds), UserDao {
    override fun get(): List<User> {
        return jdbiDao.extension<QueryUser, List<User>> { get() }
    }
}

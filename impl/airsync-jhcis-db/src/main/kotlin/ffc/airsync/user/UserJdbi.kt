package ffc.airsync.user

import ffc.airsync.Dao
import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.airsync.getLogger
import ffc.entity.User

class UserJdbi(
    val jdbiDao: Dao = MySqlJdbi(null)
) : UserDao {
    private val logger = getLogger(this)
    override fun get(): List<User> {
        return jdbiDao.extension<QueryUser, List<User>> { get() }.filter {
            kotlin.runCatching {
                val test = "${it.name}:" + it.password
                logger.debug("User test password $test")
            }.isSuccess
        }
    }
}

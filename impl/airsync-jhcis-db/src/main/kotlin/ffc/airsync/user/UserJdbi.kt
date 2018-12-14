package ffc.airsync.user

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.User
import javax.sql.DataSource

class UserJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), QueryUser {
    override fun get(): List<User> {
        return jdbiDao.extension<QueryUser, List<User>> { get() }
    }
}

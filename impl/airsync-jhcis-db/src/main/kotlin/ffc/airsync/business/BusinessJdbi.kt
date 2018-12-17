package ffc.airsync.business

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.place.Business
import javax.sql.DataSource

class BusinessJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), BusinessDao {
    override fun get(): List<Business> {
        return jdbiDao.extension<QueryBusiness, List<Business>> { get() }
    }
}

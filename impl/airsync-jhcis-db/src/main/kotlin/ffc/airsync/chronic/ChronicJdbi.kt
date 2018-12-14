package ffc.airsync.chronic

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.healthcare.Chronic
import javax.sql.DataSource

class ChronicJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), QueryChronic {
    override fun get(): List<Chronic> {
        return jdbiDao.extension<QueryChronic, List<Chronic>> { get() }
    }
}

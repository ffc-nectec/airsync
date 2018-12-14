package ffc.airsync.temple

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.place.ReligiousPlace
import javax.sql.DataSource

class TempleJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), QueryTemple {
    override fun get(): List<ReligiousPlace> {
        return jdbiDao.extension<QueryTemple, List<ReligiousPlace>> { get() }
    }
}

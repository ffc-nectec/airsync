package ffc.airsync.village

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.Village
import javax.sql.DataSource

class VillageJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), QueryVillage {
    override fun get(): List<Village> {
        return jdbiDao.extension<QueryVillage, List<Village>> { get() }
    }
}

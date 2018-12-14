package ffc.airsync.ncds

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.healthcare.NCDScreen
import javax.sql.DataSource

class NCDscreenJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), NCDscreenQuery {
    override fun createIndex() {
        jdbiDao.extension<NCDscreenQuery, Unit> { createIndex() }
    }

    override fun get(visitnumber: Long): List<NCDScreen> {
        return jdbiDao.extension<NCDscreenQuery, List<NCDScreen>> { get(visitnumber) }
    }

    override fun getAll(): List<HashMap<Long, NCDScreen>> {
        return jdbiDao.extension<NCDscreenQuery, List<kotlin.collections.HashMap<Long, NCDScreen>>> { getAll() }
    }
}

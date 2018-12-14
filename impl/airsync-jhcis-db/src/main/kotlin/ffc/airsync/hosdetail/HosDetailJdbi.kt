package ffc.airsync.hosdetail

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import javax.sql.DataSource

class HosDetailJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), QueryHosDetail {
    override fun get(): List<HashMap<String, String>> {
        return jdbiDao.extension<QueryHosDetail, List<kotlin.collections.HashMap<String, String>>> { get() }
    }
}

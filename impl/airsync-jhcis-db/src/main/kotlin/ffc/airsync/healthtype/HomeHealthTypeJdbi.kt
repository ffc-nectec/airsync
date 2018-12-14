package ffc.airsync.healthtype

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.healthcare.CommunityService
import javax.sql.DataSource

class HomeHealthTypeJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), QueryHomeHealthType {
    override fun get(healthcode: String): List<CommunityService.ServiceType> {
        return jdbiDao.extension<QueryHomeHealthType, List<CommunityService.ServiceType>> { get(healthcode) }
    }
}

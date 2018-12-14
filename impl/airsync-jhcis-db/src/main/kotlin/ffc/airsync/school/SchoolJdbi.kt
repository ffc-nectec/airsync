package ffc.airsync.school

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.place.School
import javax.sql.DataSource

class SchoolJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), QuerySchool {
    override fun get(): List<School> {
        return jdbiDao.extension<QuerySchool, List<School>> { get() }
    }
}

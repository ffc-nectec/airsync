package ffc.airsync.specialpp

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.airsync.person.QueryPerson
import javax.sql.DataSource

class SpecialPpJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), SpecialppQuery {
    override fun createIndex() {
        jdbiDao.extension<SpecialppQuery, Unit> { createIndex() }
    }

    override fun get(visitnumber: Long): List<String> {
        return jdbiDao.extension<SpecialppQuery, List<String>> { get(visitnumber) }
    }

    override fun getAll(): List<HashMap<Long, String>> {
        return jdbiDao.extension<QueryPerson, List<HashMap<Long, String>>> { getAll() }
    }
}

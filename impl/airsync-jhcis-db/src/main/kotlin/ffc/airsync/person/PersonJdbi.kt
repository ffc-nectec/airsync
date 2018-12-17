package ffc.airsync.person

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.Person
import javax.sql.DataSource

class PersonJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), PersonDao {
    override fun get(): List<Person> {
        return jdbiDao.extension<QueryPerson, List<Person>> { get() }
    }

    override fun find(pcucode: String, pid: Long): List<Person> {
        return jdbiDao.extension<QueryPerson, List<Person>> { findPerson(pcucode, pid) }
    }
}

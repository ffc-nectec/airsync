package ffc.airsync.person

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.Person
import javax.sql.DataSource

class PersonJdbi(
    ds: DataSource? = null
) : MySqlJdbi(ds), PersonDao {
    override fun get(): List<Person> {
        return jdbiDao.extension<QueryPerson, List<Person>> { get() }
    }

    override fun find(pcucode: String, pid: Long): List<Person> {
        return jdbiDao.extension<QueryPerson, List<Person>> { findPerson(pcucode, pid) }
    }
}

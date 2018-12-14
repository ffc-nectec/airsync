package ffc.airsync.visit

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.healthcare.HomeVisit
import javax.sql.DataSource

class HomeVisitIndividualJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), HomeVisitIndividualQuery {
    override fun createIndex() {
        jdbiDao.extension<HomeVisitIndividualQuery, Unit> { createIndex() }
    }

    override fun get(visitnumber: Long): List<HomeVisit> {
        return jdbiDao.extension<HomeVisitIndividualQuery, List<HomeVisit>> { get(visitnumber) }
    }

    override fun getAll(): List<HashMap<Long, HomeVisit>> {
        return jdbiDao.extension<HomeVisitIndividualQuery, List<HashMap<Long, HomeVisit>>> { getAll() }
    }

    override fun insertVitsitIndividual(insertIndividualData: InsertIndividualData) {
        jdbiDao.extension<HomeVisitIndividualQuery, Unit> { insertVitsitIndividual(insertIndividualData) }
    }

    override fun updateVitsitIndividual(insertIndividualData: InsertIndividualData) {
        jdbiDao.extension<HomeVisitIndividualQuery, Unit> { updateVitsitIndividual(insertIndividualData) }
    }
}

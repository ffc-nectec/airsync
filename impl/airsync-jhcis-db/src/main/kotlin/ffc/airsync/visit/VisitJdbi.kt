package ffc.airsync.visit

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.healthcare.HealthCareService
import javax.sql.DataSource

class VisitJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), VisitQuery {
    override fun createIndex() {
        jdbiDao.extension<VisitQuery, Unit> { createIndex() }
    }

    override fun get(): List<HealthCareService> {
        return jdbiDao.extension<VisitQuery, List<HealthCareService>> { get() }
    }

    override fun get(whereString: String): List<HealthCareService> {
        return jdbiDao.extension<VisitQuery, List<HealthCareService>> { get(whereString) }
    }

    override fun getMaxVisitNumber(): List<Long> {
        return jdbiDao.extension<VisitQuery, List<Long>> { getMaxVisitNumber() }
    }

    override fun insertVisit(homeInsert: List<InsertData>) {
        jdbiDao.extension<VisitQuery, Unit> { insertVisit(homeInsert) }
    }

    override fun inserVisit(pcuCode: String, visitNumber: Long) {
        jdbiDao.extension<VisitQuery, Unit> { inserVisit(pcuCode, visitNumber) }
    }

    override fun updateVisit(homeInsert: InsertData) {
        jdbiDao.extension<VisitQuery, Unit> { updateVisit(homeInsert) }
    }
}

package ffc.airsync.visit

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.healthcare.Diagnosis
import javax.sql.DataSource

class VisitDiagJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), VisitDiagQuery {
    override fun createIndex() {
        jdbiDao.extension<VisitDiagQuery, Unit> { createIndex() }
    }

    override fun getDiag(visitnumber: Long): List<Diagnosis> {
        return jdbiDao.extension<VisitDiagQuery, List<Diagnosis>> { getDiag(visitnumber) }
    }

    override fun insertVisitDiag(insertDiagData: Iterable<InsertDiagData>) {
        jdbiDao.extension<VisitDiagQuery, Unit> { insertVisitDiag(insertDiagData) }
    }

    override fun updateVisitDiag(insertDiagData: InsertDiagData) {
        jdbiDao.extension<VisitDiagQuery, Unit> { updateVisitDiag(insertDiagData) }
    }
}

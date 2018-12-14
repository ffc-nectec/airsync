package ffc.airsync.disease

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.healthcare.Disease
import javax.sql.DataSource

class DiseaseJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), QueryDisease {
    override fun get(icd10: String): List<Disease> {
        return jdbiDao.extension<QueryDisease, List<Disease>> { get(icd10) }
    }
}

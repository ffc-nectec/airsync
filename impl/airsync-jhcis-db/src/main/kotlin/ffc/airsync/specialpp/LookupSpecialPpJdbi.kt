package ffc.airsync.specialpp

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.healthcare.SpecialPP
import javax.sql.DataSource

class LookupSpecialPpJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), LookupSpecialPP {
    override fun get(ppcode: String): List<SpecialPP.PPType> {
        return jdbiDao.extension<LookupSpecialPP, List<SpecialPP.PPType>> { get(ppcode) }
    }
}

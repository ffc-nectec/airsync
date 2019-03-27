package ffc.airsync.mysqlvariable

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import javax.sql.DataSource

class MySqlVariableJdbi(
    ds: DataSource? = null
) : MySqlJdbi(ds), GetMySqlVariable {
    override fun mysqlLocation(): String {
        return jdbiDao.extension<QueryMySqlVariable, String> { getBaseDir().first() }
    }

    override fun mysqlDataDirectory(): String {
        return jdbiDao.extension<QueryMySqlVariable, String> { getDataDir().first() }
    }
}

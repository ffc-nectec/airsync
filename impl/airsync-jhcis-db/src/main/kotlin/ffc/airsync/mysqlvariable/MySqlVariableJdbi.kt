package ffc.airsync.mysqlvariable

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension

class MySqlVariableJdbi(
    val jdbiDao: MySqlJdbi = MySqlJdbi(null)
) : GetMySqlVariable {
    override fun mysqlLocation(): String {
        return jdbiDao.extension<QueryMySqlVariable, String> { getBaseDir().first() }
    }

    override fun mysqlDataDirectory(): String {
        return jdbiDao.extension<QueryMySqlVariable, String> { getDataDir().first() }
    }
}

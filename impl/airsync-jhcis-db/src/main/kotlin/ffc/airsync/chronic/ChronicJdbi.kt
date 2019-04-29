package ffc.airsync.chronic

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.healthcare.Chronic

class ChronicJdbi(
    val jdbiDao: MySqlJdbi = MySqlJdbi(null)
) : ChronicDao {
    override fun get(): List<Chronic> = jdbiDao.extension<QueryChronic, List<Chronic>> { get() }
}

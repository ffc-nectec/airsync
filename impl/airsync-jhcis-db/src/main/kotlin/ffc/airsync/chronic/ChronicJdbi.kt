package ffc.airsync.chronic

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.healthcare.Chronic
import javax.sql.DataSource

class ChronicJdbi(
    ds: DataSource? = null
) : MySqlJdbi(ds), ChronicDao {
    override fun get(): List<Chronic> = jdbiDao.extension<QueryChronic, List<Chronic>> { get() }
}

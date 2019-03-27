package ffc.airsync.temple

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.place.ReligiousPlace
import javax.sql.DataSource

class TempleJdbi(
    ds: DataSource? = null
) : MySqlJdbi(ds), QueryTemple {
    override fun get(): List<ReligiousPlace> {
        return jdbiDao.extension<QueryTemple, List<ReligiousPlace>> { get() }
    }
}

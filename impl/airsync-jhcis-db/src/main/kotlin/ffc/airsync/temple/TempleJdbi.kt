package ffc.airsync.temple

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.place.ReligiousPlace

class TempleJdbi(
    val jdbiDao: MySqlJdbi = MySqlJdbi(null)
) : QueryTemple {
    override fun get(): List<ReligiousPlace> {
        return jdbiDao.extension<QueryTemple, List<ReligiousPlace>> { get() }
    }
}

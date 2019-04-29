package ffc.airsync.village

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.Village

class VillageJdbi(
    val jdbiDao: MySqlJdbi = MySqlJdbi(null)
) : VillageDao {
    override fun get(): List<Village> {
        return jdbiDao.extension<QueryVillage, List<Village>> { get() }
    }
}

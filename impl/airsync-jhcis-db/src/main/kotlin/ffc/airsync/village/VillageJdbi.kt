package ffc.airsync.village

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.Village
import javax.sql.DataSource

class VillageJdbi(
    ds: DataSource? = null
) : MySqlJdbi(ds), VillageDao {
    override fun get(): List<Village> {
        return jdbiDao.extension<QueryVillage, List<Village>> { get() }
    }
}

package ffc.airsync.business

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.place.Business
import javax.sql.DataSource

class BusinessJdbi(
    ds: DataSource? = null
) : MySqlJdbi(ds), BusinessDao {
    override fun get(): List<Business> {
        return jdbiDao.extension<QueryBusiness, List<Business>> { get() }
    }
}

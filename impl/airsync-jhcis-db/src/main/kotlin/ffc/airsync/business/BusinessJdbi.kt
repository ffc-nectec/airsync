package ffc.airsync.business

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.place.Business

class BusinessJdbi(
    val jdbiDao: MySqlJdbi = MySqlJdbi(null)
) : BusinessDao {
    override fun get(): List<Business> {
        return jdbiDao.extension<QueryBusiness, List<Business>> { get() }
    }
}

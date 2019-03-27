package ffc.airsync.foodshop

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.place.Business
import javax.sql.DataSource

class FoodShopJdbi(
    ds: DataSource? = null
) : MySqlJdbi(ds), QueryFoodShop {
    override fun get(): List<Business> {
        return jdbiDao.extension<QueryFoodShop, List<Business>> { get() }
    }
}

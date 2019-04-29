package ffc.airsync.foodshop

import ffc.airsync.Dao
import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.place.Business

class FoodShopJdbi(
    val jdbiDao: Dao = MySqlJdbi(null)
) : QueryFoodShop {
    override fun get(): List<Business> {
        return jdbiDao.extension<QueryFoodShop, List<Business>> { get() }
    }
}

package ffc.airsync.foodshop

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.place.Business

class FoodShopJdbi(
    val jdbiDao: MySqlJdbi = MySqlJdbi(null)
) : QueryFoodShop {
    override fun get(): List<Business> {
        return jdbiDao.extension<QueryFoodShop, List<Business>> { get() }
    }
}

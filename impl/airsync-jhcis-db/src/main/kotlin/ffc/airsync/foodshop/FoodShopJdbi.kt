package ffc.airsync.foodshop

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.place.Business
import javax.sql.DataSource

class FoodShopJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), QueryFoodShop {
    override fun get(): List<Business> {
        return jdbiDao.extension<QueryFoodShop, List<Business>> { get() }
    }
}

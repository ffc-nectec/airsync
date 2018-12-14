package ffc.airsync.house

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.place.House
import javax.sql.DataSource

class HouseJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), QueryHouse {
    override fun update(house: HouseJhcisDb) {
        jdbiDao.extension<QueryHouse, Unit> { update(house) }
    }

    override fun findThat(): List<House> {
        return jdbiDao.extension<QueryHouse, List<House>> { findThat() }
    }

    override fun findThat(whereString: String): List<House> {
        return jdbiDao.extension<QueryHouse, List<House>> { findThat(whereString) }
    }
}

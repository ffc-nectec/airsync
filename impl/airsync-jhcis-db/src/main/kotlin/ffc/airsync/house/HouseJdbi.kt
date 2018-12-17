package ffc.airsync.house

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.airsync.utils.printDebug
import ffc.entity.gson.toJson
import ffc.entity.place.House
import java.sql.Timestamp
import javax.sql.DataSource

class HouseJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), HouseDao {
    override fun getHouse(): List<House> {
        val houses = jdbiDao.extension<QueryHouse, List<House>> { findThat() }
        houses.forEachIndexed { index, house ->
            printDebug("HouseXY = " + house.location + ", " + index)
        }
        return houses
    }

    override fun getHouse(whereString: String): List<House> {
        if (whereString.isBlank()) return arrayListOf()
        return jdbiDao.extension<QueryHouse, List<House>> { findThat(whereString) }
    }

    override fun upateHouse(house: House) {
        val houseUpdate = HouseJhcisDb(
            hid = house.identity?.id,
            road = house.road,
            xgis = house.location?.coordinates?.longitude?.toString(),
            ygis = house.location?.coordinates?.latitude?.toString(),
            hno = house.no,
            dateUpdate = Timestamp(house.timestamp.plusHours(7).millis),

            pcucode = house.link!!.keys["pcucode"].toString(),
            hcode = house.link!!.keys["hcode"].toString().toInt()
        )
        printDebug("House update from could = ${houseUpdate.toJson()}")
        jdbiDao.extension<QueryHouse, Any> { update(houseUpdate) }
        printDebug("\tFinish upateHouse")
    }
}

package ffc.airsync.house

import ffc.airsync.Dao
import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.airsync.getLogger
import ffc.entity.Village
import ffc.entity.gson.toJson
import ffc.entity.place.House
import java.sql.Timestamp

class HouseJdbi(
    val jdbiDao: Dao = MySqlJdbi(null)
) : HouseDao {
    private val logger by lazy { getLogger(this) }
    override fun getHouse(lookupVillage: (jVillageId: String) -> Village?): List<House> {
        val houses = jdbiDao.extension<QueryHouse, List<House>> { findThat() }
        houses.forEachIndexed { index, house ->
            val village = lookupVillage(house.link?.keys?.get("villcode")?.toString() ?: "")
            if (village != null) {
                house.villageId = village.id
                house.villageName = village.name
            }

            logger.trace("HouseXY = " + house.location + ", " + index)
        }
        return houses
    }

    override fun getHouse(lookupVillage: (jVillageId: String) -> Village?, whereString: String): List<House> {
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
        logger.info("House update from could = ${houseUpdate.toJson()}")
        jdbiDao.extension<QueryHouse, Any> { update(houseUpdate) }
        logger.debug("\tFinish upateHouse")
    }
}

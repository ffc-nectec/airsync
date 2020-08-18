/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
    private fun getHouseNoWhere(lookupVillage: (jVillageId: String) -> Village?): List<House> {
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
        if (whereString.isBlank()) return getHouseNoWhere(lookupVillage)
        return jdbiDao.extension<QueryHouse, List<House>> { findThat(whereString) }
    }

    override fun upateHouse(house: House) {
        val houseUpdate = HouseJhcisDb(
            hid = house.identity?.id,
            road = house.road,
            xgis = house.location?.coordinates?.latitude?.toString(),
            ygis = house.location?.coordinates?.longitude?.toString(),
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

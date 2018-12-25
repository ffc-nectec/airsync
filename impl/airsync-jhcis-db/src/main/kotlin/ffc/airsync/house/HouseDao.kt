package ffc.airsync.house

import ffc.entity.Village
import ffc.entity.place.House

interface HouseDao {
    fun getHouse(
        lookupVillage: (jVillageId: String) -> Village?
    ): List<House>

    fun getHouse(
        lookupVillage: (jVillageId: String) -> Village?,
        whereString: String
    ): List<House>

    fun upateHouse(house: House)
}

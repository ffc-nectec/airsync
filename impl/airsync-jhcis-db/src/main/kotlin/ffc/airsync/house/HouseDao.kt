package ffc.airsync.house

import ffc.entity.place.House

interface HouseDao {
    fun getHouse(): List<House>
    fun getHouse(whereString: String): List<House>
    fun upateHouse(house: House)
}

package ffc.airsync.api

import ffc.airsync.db.DatabaseDao
import ffc.entity.House

interface HouseApi {
    fun putHouse(houseList: List<House>): List<House>
    fun syncHouseFromCloud(_id: String, databaseDao: DatabaseDao)
    fun syncHouseToCloud(house: House)
}

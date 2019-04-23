package ffc.airsync.api.house

import ffc.airsync.db.DatabaseDao
import ffc.entity.place.House

interface HouseApi {
    fun putHouse(houseList: List<House>, progressCallback: (Int) -> Unit): List<House>
    fun syncHouseFromCloud(_id: String, databaseDao: DatabaseDao)
    fun syncHouseToCloud(house: House)
}

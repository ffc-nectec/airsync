package ffc.airsync.api.house

import ffc.airsync.db.DatabaseDao
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.UploadSpliter
import ffc.airsync.utils.printDebug
import ffc.entity.House

class RetofitHouseApi : RetofitApi(), HouseApi {
    override fun putHouse(houseList: List<House>): List<House> {
        val houseLastUpdate = arrayListOf<House>()
        UploadSpliter.upload(100, houseList, object : UploadSpliter.HowToSendCake<House> {
            override fun send(cakePlate: ArrayList<House>) {
                val respond = restService.createHouse(
                    orgId = organization.id,
                    authkey = tokenBarer,
                    houseList = cakePlate
                ).execute()
                if (respond.code() != 201) throw IllegalAccessException("Cannot Login ${respond.code()}")
                val houseFromCloud = respond.body() ?: arrayListOf()
                houseLastUpdate.addAll(houseFromCloud)
            }
        })
        return houseLastUpdate
    }

    override fun syncHouseFromCloud(_id: String, databaseDao: DatabaseDao) {
        printDebug("Sync From Cloud get house house _id = $_id")
        val data = restService.getHouse(orgId = organization.id, authkey = tokenBarer, _id = _id).execute()
        printDebug("\tRespond code ${data.code()}")
        val house = data.body() ?: throw IllegalArgumentException("ไม่มี เลขบ้าน getHouse")
        printDebug("\t From house cloud _id = ${house.id} house No. ${house.no}")
        if (house.link?.isSynced == true) return

        databaseDao.upateHouse(house)
        printDebug("\tUpdate house to database and sync = true")
        house.link?.isSynced = true

        printDebug("\tPut new house to cloud")
        restService.putHouse(orgId = organization.id, authkey = tokenBarer, _id = _id, house = house).execute()
    }

    override fun syncHouseToCloud(house: House) {
        restService.putHouse(orgId = organization.id, authkey = tokenBarer, _id = house.id, house = house).execute()
    }
}

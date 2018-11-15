package ffc.airsync.api.house

import ffc.airsync.db.DatabaseDao
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.UploadSpliter
import ffc.airsync.utils.printDebug
import ffc.entity.place.House

class RetofitHouseApi : RetofitApi(), HouseApi {
    override fun putHouse(houseList: List<House>): List<House> {
        var loop = 0
        while (true) {
            try {
                println("Start put house to cloud")
                val houseLastUpdate = arrayListOf<House>()
                restService.clernHouse(orgId = organization.id, authkey = tokenBarer).execute()
                UploadSpliter.upload(100, houseList) {
                    val respond = restService.createHouse(
                        orgId = organization.id,
                        authkey = tokenBarer,
                        houseList = it
                    ).execute()
                    if (respond.code() != 201)
                        throw IllegalAccessException(
                            "Error ${respond.code()} ${respond.errorBody()?.charStream()?.readText()}"
                        )
                    val houseFromCloud = respond.body() ?: arrayListOf()
                    houseLastUpdate.addAll(houseFromCloud)
                }

                return houseLastUpdate
            } catch (ex: java.net.SocketTimeoutException) {
                println("Time out loop ${++loop}")
                ex.printStackTrace()
            }
        }
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

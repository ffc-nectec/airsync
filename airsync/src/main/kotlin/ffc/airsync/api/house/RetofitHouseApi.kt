package ffc.airsync.api.house

import ffc.airsync.db.DatabaseDao
import ffc.airsync.printDebug
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.UploadSpliter
import ffc.airsync.utils.callApi
import ffc.airsync.utils.callApiNoReturn
import ffc.entity.place.House

class RetofitHouseApi : RetofitApi<HouseUrl>(HouseUrl::class.java), HouseApi {
    override fun putHouse(houseList: List<House>, progressCallback: (Int) -> Unit): List<House> {
        callApiNoReturn { restService.clernHouse(orgId = organization.id, authkey = tokenBarer).execute() }

        printDebug("Start put house to cloud")
        val houseLastUpdate = arrayListOf<House>()
        val houseSize = houseList.size
        UploadSpliter.upload(100, houseList) { it, index ->

            val result = callApi {
                restService.unConfirmHouseBlock(
                    orgId = organization.id,
                    authkey = tokenBarer,
                    block = index
                ).execute()

                val respond = restService.insertHouseBlock(
                    orgId = organization.id,
                    authkey = tokenBarer,
                    houseList = it,
                    block = index
                ).execute()
                if (respond.code() == 201 || respond.code() == 200) {
                    restService.confirmHouseBlock(
                        orgId = organization.id,
                        authkey = tokenBarer,
                        block = index
                    ).execute()
                    progressCallback(((index * 50) / houseSize) + 50)
                    respond.body() ?: arrayListOf()
                } else {
                    throw ApiLoopException("Error ${respond.code()} ${respond.errorBody()?.charStream()?.readText()}")
                }
            }
            houseLastUpdate.addAll(result)
        }
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

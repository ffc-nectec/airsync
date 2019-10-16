package ffc.airsync.api.house

import ffc.airsync.db.DatabaseDao
import ffc.airsync.gui
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.UploadSpliter
import ffc.airsync.utils.callApi
import ffc.airsync.utils.callApiNoReturn
import ffc.airsync.utils.getLogger
import ffc.entity.place.House

class RetofitHouseApi : RetofitApi<HouseUrl>(HouseUrl::class.java), HouseApi {
    private val logger by lazy { getLogger(this) }
    override fun putHouse(houseList: List<House>, progressCallback: (Int) -> Unit): List<House> {
        callApiNoReturn { restService.clernHouse(orgId = organization.id, authkey = tokenBarer).execute() }

        logger.info("Start put house to cloud")
        val houseLastUpdate = arrayListOf<House>()
        val fixSizeCake = 100
        val houseSize = houseList.size / fixSizeCake
        UploadSpliter.upload(fixSizeCake, houseList) { it, index ->

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
                    respond.body() ?: arrayListOf()
                } else {
                    throw ApiLoopException("Error ${respond.code()} ${respond.errorBody()?.charStream()?.readText()}")
                }
            }
            houseLastUpdate.addAll(result)
            progressCallback(((index * 50) / houseSize) + 50)
        }
        return houseLastUpdate
    }

    override fun syncHouseFromCloud(_id: String, databaseDao: DatabaseDao) {
        logger.info("Sync From Cloud get house house _id = $_id")
        val data = restService.getHouse(orgId = organization.id, authkey = tokenBarer, _id = _id).execute()
        logger.debug("\tRespond code ${data.code()}")
        val house = data.body() ?: throw IllegalArgumentException("ไม่มี เลขบ้าน getHouse")
        logger.debug("\t From house cloud _id = ${house.id} house No. ${house.no}")
        if (house.link?.isSynced == true) return

        databaseDao.upateHouse(house)
        logger.debug("\tUpdate house to database and sync = true")
        house.link?.isSynced = true

        logger.info("\tPut new house to cloud")
        restService.putHouse(orgId = organization.id, authkey = tokenBarer, _id = _id, house = house).execute()
        gui.createMessageDelay("พบข้อมูล update บ้าน")
    }

    override fun syncHouseToCloud(house: House) {
        restService.putHouse(orgId = organization.id, authkey = tokenBarer, _id = house.id, house = house).execute()
    }
}

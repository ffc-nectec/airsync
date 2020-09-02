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

package ffc.airsync.api.house

import ffc.airsync.gui
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.ui.AirSyncGUI.MESSAGE_TYPE.INFO
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.UploadSpliter
import ffc.airsync.utils.callApi
import ffc.airsync.utils.callApiNoReturn
import ffc.airsync.utils.getLogger
import ffc.entity.place.House

class HouseServiceApi : RetofitApi<HouseService>(HouseService::class.java), HouseApi {
    private val logger by lazy { getLogger(this) }
    override fun createHouse(
        houseList: List<House>,
        progressCallback: (Int) -> Unit,
        clearCloud: Boolean
    ): List<House> {
        if (clearCloud)
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
            if (houseSize != 0)
                progressCallback(((index * 50) / houseSize) + 50)
        }
        return houseLastUpdate
    }

    override fun get(houseId: String): House? {
        logger.info("Sync From Cloud get house house _id = $houseId")
        val house = getHouse(houseId)
        logger.debug("\t From house cloud _id = ${house.id} house No. ${house.no}")
        if (house.link?.isSynced == true) return null

        gui.createMessageDelay("กำลังดึงข้อมูลบ้านเลขที่\r\n${house.no} จาก Cloud", INFO, 60000)
        logger.debug("\tUpdate house to database and sync = true")
        house.link?.isSynced = true

        logger.info("\tPut new house to cloud")
        restService.putHouse(orgId = organization.id, authkey = tokenBarer, _id = houseId, house = house).execute()

        return house
    }

    private fun getHouse(_id: String): House {
        val data = restService.getHouse(orgId = organization.id, authkey = tokenBarer, _id = _id).execute()
        logger.debug("\tRespond code ${data.code()}")
        return data.body() ?: throw IllegalArgumentException("ไม่มี เลขบ้าน getHouse")
    }

    override fun update(house: House): House {
        gui.createMessageDelay("กำลังส่งข้อมูลบ้านเลขที่\r\n${house.no} ไปยัง Cloud", INFO, 9000)
        restService.putHouse(orgId = organization.id, authkey = tokenBarer, _id = house.id, house = house).execute()
        return getHouse(house.id)
    }

    override fun update(houses: List<House>): List<House> {
        val output = arrayListOf<House>()
        UploadSpliter.upload(100, houses) { it, index ->
            val response = callApi {
                restService.putHouses(organization.id, tokenBarer, it).execute()
            }
            if (response.code() == 200 || response.code() == 201) {
                output.addAll(response.body()!!)
            } else {
                throw ApiLoopException("Cannot update person ${response.code()}")
            }
        }
        return output
    }
}

/*
 * Copyright (c) 2561 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.airsync.api.services.module

import ffc.airsync.api.dao.ActionListDao
import ffc.airsync.api.dao.DaoFactory
import ffc.model.*
import me.piruin.geok.geometry.Feature
import me.piruin.geok.geometry.FeatureCollection
import me.piruin.geok.geometry.Point
import java.util.*
import javax.ws.rs.BadRequestException
import javax.ws.rs.NotAuthorizedException
import javax.ws.rs.NotFoundException
import kotlin.collections.ArrayList

object HouseService {

    val houseDao = DaoFactory().buildHouseDao()
    val actionList = DaoFactory().buildActionDao()

    fun create(token: String, orgId: String, houseList: List<Address>) {
        val org = getOrgByOrgToken(token, orgId)

        //For org
        houseList.forEach {
            if (it.hid!! < 0) throw BadRequestException("")
        }

        houseDao.insert(org.uuid, houseList)
    }

    fun update(token: String, orgId: String, house: Address, house_id: String) {
        //val org = getOrgByMobileToken(token = UUID.fromString(token), orgId = orgId)

        if (house._id == "") throw BadRequestException("ไม่มี _id")

        house.people = null
        house.haveChronics = null
        if (house_id == house._id) {

            var orgUuid: UUID
            var updateTo = ActionHouse.UPDATETO.ORG

            try {
                orgUuid = getOrgByMobileToken(token = UUID.fromString(token), orgId = orgId).uuid
            } catch (ex: NotAuthorizedException) {
                orgUuid = getOrgByOrgToken(token, orgId).uuid
                updateTo = ActionHouse.UPDATETO.MOBILE
            }


            houseDao.update(house.clone())
            printDebug("Call add ActionHouse")


            val actionHouse = ActionHouse(orgUuid = orgUuid, action = house.clone(), updateTo = updateTo)
            actionList.insert(actionHouse)
        } else {
            printDebug("House id not eq update houseIdParameter=$house_id houseIdInData=${house._id}")
            throw BadRequestException("House _id not eq update")
        }
    }

    fun getAction(token: String, orgId: String, updateTo: ActionHouse.UPDATETO = ActionHouse.UPDATETO.ORG): List<ActionHouse> {
        //val orgToken = getOrgByMobileToken(token = UUID.fromString(token), orgId = orgId) // ตรวจสอบ Token จาก Mobile
        val org = getOrgByOrgToken(token, orgId)
        return actionList.get(orgUUID = org.uuid, updateTo = updateTo)
    }

    fun updateActionComplete(token: String, orgId: String, actionId: UUID) {
        //val orgToken = getOrgByMobileToken(token = UUID.fromString(token), orgId = orgId) // ตรวจสอบ Token จาก Mobile
        val org = getOrgByOrgToken(token, orgId)

        actionList.updateStatusComplete(actionId)

    }

    fun get(token: String, orgId: String, page: Int = 1, per_page: Int = 200, hid: Int = -1): FeatureCollection<Address> {


        printDebug("Token = $token")

        val tokenObj = getOrgByMobileToken(UUID.fromString(token.trim()), orgId)

        printDebug("Befor check token")
        printDebug(tokenObj)


        printDebug("Search house match")

        val houseList: List<StorageOrg<Address>>

        if (hid > 0) {
            val house = houseDao.findByHouseId(tokenObj.uuid, hid)
              ?: throw NotFoundException("ไม่พบ hid บ้าน")
            houseList = ArrayList()
            houseList.add(house)
        } else {
            houseList = houseDao.find(tokenObj.uuid)
        }


        printDebug("count house = ${houseList.count()}")

        val geoJson = FeatureCollection<Address>()


        //val peopleInHouse=HashMap<String,ArrayList<People>>()


        val fromItem = ((page - 1) * per_page) + 1
        var toItem = (page) * per_page
        val count = houseList.count()

        if (fromItem > count)
            throw NotFoundException("Query เกินหน้าสุดท้ายของบ้านแล้ว")

        if (toItem > count)
            toItem = count

        printDebug("page $page per_page $per_page")
        printDebug("from $fromItem to $toItem")


        (fromItem..toItem).forEach {
            printDebug("Loop count $it")
            val data = houseList[it - 1]


            val point = Point(data.data.coordinates!!)
            //printDebug(geometry)
            //printDebug(properits)
            val houseId = data.data.hid ?: -1
            //printDebug(houseId)
            val house = data.data
            //printDebug(house)

            house.haveChronics = chronicDao.houseIsChronic(tokenObj.uuid, houseId)
            house.people = personDao.getPeopleInHouse(tokenObj.uuid, houseId)


            printDebug("Create feture")
            val feture: Feature<Address> = Feature(geometry = point
              , properties = house)

            printDebug("Add feture")
            geoJson.features.add(feture)
            printDebug("Add feture success")

        }


        //printDebug("Feture gson count ${geoJson.features.count()}")

        //printDebug("For each house")
        /* houseList.forEach {




         }*/

        return geoJson


    }

    fun getSingleHouse(token: String, orgId: String, houseId: String): Address {

        var orgUuid: UUID

        try {
            orgUuid = getOrgByMobileToken(token = UUID.fromString(token), orgId = orgId).uuid
        } catch (ex: NotAuthorizedException) {
            orgUuid = getOrgByOrgToken(token, orgId).uuid
        }


        val house = houseDao.findByHouseId(orgUuid, houseId.toInt())?.data

        return house ?: throw NotFoundException("ไม่มีรายการบ้าน ที่ระบุ")


    }

}

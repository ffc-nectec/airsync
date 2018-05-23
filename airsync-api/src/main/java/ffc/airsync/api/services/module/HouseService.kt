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

import com.google.firebase.messaging.Message
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

    fun create(token: String, orgId: String, houseList: List<Address>) {
        val org = getOrgByOrgToken(token, orgId)

        houseList.forEach {
            if (it.hid!! < 0) throw BadRequestException("")
        }

        houseDao.insert(org.uuid, houseList)
    }

    fun create(token: String, orgId: String, house: Address) {
        val org = getOrgByOrgToken(token, orgId)
        if (house.hid!! < 0) throw BadRequestException("")
        houseDao.insert(org.uuid, house)
    }


    fun update(token: String, orgId: String, house: Address, house_id: String) {

        printDebug("Update house token $token orgid $orgId house_id $house_id house ${house.toJson()}")
        if (house._id == "") throw BadRequestException("ไม่มี _id")


        house.people = null
        house.haveChronics = null


        if (house_id == house._id) {
            var _sync = false
            val firebaseTokenGropOrg = arrayListOf<String>()
            var mobileList: List<StorageOrg<MobileToken>>? = null
            var org: Organization? = null


            try {
                printDebug("\tFind mobile token")
                val mobile = getOrgByMobileToken(token = UUID.fromString(token), orgId = orgId)
                mobileList = tokenMobile.findByOrgUuid(mobile.uuid)
                org = orgDao.findByUuid(mobile.uuid)
                printDebug("\t\tFound mobile token")


            } catch (ex: NotAuthorizedException) {
                printDebug("\tFind org token")
                val organize = getOrgByOrgToken(token, orgId)
                _sync = true
                mobileList = tokenMobile.findByOrgUuid(organize.uuid)
                org = organize
                printDebug("\t\tFound org token")


            } finally {
                printDebug("\tGroup firebase token")
                mobileList?.forEach {
                    firebaseTokenGropOrg.add(it.data.firebaseToken ?: "")
                    printDebug("\tmobile $it")
                }


                firebaseTokenGropOrg.add(org?.firebaseToken ?: "")
                printDebug("\torg ${org?.firebaseToken}")
            }



            house._sync = _sync
            houseDao.update(house.clone())


            printDebug("Call send notification size list token = ${firebaseTokenGropOrg.size} ")
            firebaseTokenGropOrg.forEach {
                printDebug("\ttoken=$it")
                if (it.isNotEmpty())
                    Message.builder().putHouseData(house, it, orgId)
            }


        } else {  //ทำงานเมื่อ _id ใน url ไม่ตรงกับ _id ที่อยู่ในข้อมูลที่ส่งเข้ามา update
            printDebug("House id not eq update houseIdParameter=$house_id houseIdInData=${house._id}")
            throw BadRequestException("House _id not eq update")
        }
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
        val count = houseList.count()

        itemRenderPerPage(page, per_page, count, object : AddItmeAction {
            override fun onAddItemAction(itemIndex: Int) {
                //printDebug("Loop count $it")
                val data = houseList[itemIndex]
                val feture = createGeo(data.data, tokenObj.uuid)
                geoJson.features.add(feture)
                //printDebug("Add feture success")
            }
        })

        return geoJson
    }

    fun getSingle(token: String, orgId: String, houseId: String): Address {
        var orgUuid: UUID


        try {
            orgUuid = getOrgByMobileToken(token = UUID.fromString(token), orgId = orgId).uuid
        } catch (ex: NotAuthorizedException) {
            orgUuid = getOrgByOrgToken(token, orgId).uuid
        }


        val house = houseDao.findByHouse_Id(orgUuid, houseId)?.data
        return house ?: throw NotFoundException("ไม่มีรายการบ้าน ที่ระบุ")
    }

    fun getSingleGeo(token: String, orgId: String, houseId: String): FeatureCollection<Address> {

        val tokenObj = getOrgByMobileToken(UUID.fromString(token.trim()), orgId)
        val geoJson = FeatureCollection<Address>()
        val house: StorageOrg<Address>


        printDebug("\thouse findBy_ID OrgUuid = ${tokenObj.uuid} houseId = $houseId")
        house = houseDao.findByHouse_Id(tokenObj.uuid, houseId) ?: throw NotFoundException("ไม่พบข้อมูลบ้านที่ระบุ")


        printDebug("\t\t$house")
        val feture = createGeo(house.data, tokenObj.uuid)
        geoJson.features.add(feture)


        return geoJson
    }


    private fun createGeo(data: Address, orgUuid: UUID): Feature<Address> {
        val point = Point(data.coordinates!!)
        val houseId = data.hid ?: -1
        val house = data

        house.people = personDao.getPeopleInHouse(orgUuid, houseId)

        house.haveChronics = houseIsChronic(house.people)


        printDebug("Create feture")
        val feture: Feature<Address> = Feature(
          geometry = point,
          properties = house)


        return feture
    }

    private fun houseIsChronic(peopleList: List<People>?): Boolean {

        if (peopleList == null) return false
        val personChronic = peopleList.find {
            it.chronics != null
        }
        return personChronic != null


    }


}



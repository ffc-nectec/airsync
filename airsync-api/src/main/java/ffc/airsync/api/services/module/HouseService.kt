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
import org.joda.time.DateTime
import java.util.*
import javax.ws.rs.BadRequestException
import javax.ws.rs.NotAuthorizedException
import javax.ws.rs.NotFoundException
import kotlin.collections.ArrayList

object HouseService {

    val houseDao = DaoFactory().buildHouseDao()

    fun create(token: UUID, orgId: String, houseList: List<Address>) {
        val org = getOrgByOrgToken(token, orgId)

        houseList.forEach {
            if (it.hid!! < 0) throw BadRequestException("")
        }

        houseDao.insert(org.uuid, houseList)
    }

    fun create(token: UUID, orgId: String, house: Address) {
        val org = getOrgByOrgToken(token, orgId)
        if (house.hid!! < 0) throw BadRequestException("")
        houseDao.insert(org.uuid, house)
    }


    fun update(token: UUID, orgId: String, house: Address, house_id: String) {

        printDebug("Update house token $token orgid $orgId house_id $house_id house ${house.toJson()}")
        if (house._id == "") throw BadRequestException("ไม่มี _id")


        house.people = null
        house.haveChronics = null


        if (house_id == house._id) {
            var _sync = false
            val firebaseTokenGropOrg = arrayListOf<String>()
            var listMessage: List<StorageOrg<TokenMessage>>? = null
            var org: Organization? = null

            var isMobile = false

            try {
                printDebug("\tFind mobile token")
                val mobile = getOrgByMobileToken(token = token, orgId = orgId)
                listMessage = tokenMobile.findByOrgUuid(mobile.uuid)
                org = orgDao.findByUuid(mobile.uuid)
                isMobile = true
                printDebug("\t\tFound mobile token")


            } catch (ex: NotAuthorizedException) {
                printDebug("\tFind org token")
                val organize = getOrgByOrgToken(token, orgId)
                _sync = true
                listMessage = tokenMobile.findByOrgUuid(organize.uuid)
                org = organize
                printDebug("\t\tFound org token")


            } finally {
                printDebug("\tGroup firebase token")
                listMessage?.forEach {
                    firebaseTokenGropOrg.add(it.data.firebaseToken ?: "")
                    printDebug("\tmobile $it")
                }


                firebaseTokenGropOrg.add(org?.firebaseToken ?: "")
                printDebug("\torg ${org?.firebaseToken}")
            }



            if (isMobile) {
                house.dateUpdate = DateTime.now()
            }

            house._sync = _sync
            try {
                houseDao.update(house.clone())
            } catch (ex: Exception) {
                //ex.printStackTrace()
                throw ex
            }



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
        Thread.sleep(200)
    }


    fun getGeoJsonHouse(token: UUID, orgId: String, page: Int = 1, per_page: Int = 200, hid: Int = -1): FeatureCollection<Address> {

        printDebug("Token = $token")
        val tokenObj = getOrgByMobileToken(token, orgId)
        printDebug("Befor check token")
        printDebug(tokenObj)


        printDebug("Search house match")
        val listHouse: List<StorageOrg<Address>>


        if (hid > 0) {
            val house = houseDao.findByHouseId(tokenObj.uuid, hid)
              ?: throw NotFoundException("ไม่พบ hid บ้าน")
            listHouse = ArrayList()
            listHouse.add(house)
        } else {
            listHouse = houseDao.find(tokenObj.uuid)
        }
        printDebug("count house = ${listHouse.count()}")


        val geoJson = FeatureCollection<Address>()
        val count = listHouse.count()

        itemRenderPerPage(page, per_page, count, object : AddItmeAction {
            override fun onAddItemAction(itemIndex: Int) {
                //printDebug("Loop count $it")
                val data = listHouse[itemIndex]
                val feture = createGeo(data.data, tokenObj.uuid)
                geoJson.features.add(feture)
                //printDebug("Add feture success")
            }
        })

        return geoJson
    }

    fun getJsonHouse(token: UUID, orgId: String, page: Int = 1, per_page: Int = 200, hid: Int = -1): List<Address> {

        val geoJsonHouse = getGeoJsonHouse(token, orgId, page, per_page, hid)


        val houseList = arrayListOf<Address>()


        geoJsonHouse.features.forEach {
            val house = it.properties
            if (house != null)
                houseList.add(house)
        }

        return houseList

    }

    fun getSingle(token: UUID, orgId: String, houseId: String): Address {
        var orgUuid: UUID

        val singleHouseGeo = getSingleGeo(token, orgId, houseId)
        val house = singleHouseGeo.features.get(0).properties

        return house ?: throw NotFoundException("ไม่มีรายการบ้าน ที่ระบุ")
    }

    fun getSingleGeo(token: UUID, orgId: String, houseId: String): FeatureCollection<Address> {

        var tokenObjUuid: UUID

        try {
            tokenObjUuid = getOrgByMobileToken(token, orgId).uuid
        } catch (ex: javax.ws.rs.NotAuthorizedException) {
            tokenObjUuid = getOrgByOrgToken(token, orgId).uuid ?: throw NotAuthorizedException("ไม่มี token นี้ในระบบ")
        }


        val geoJson = FeatureCollection<Address>()
        val house: StorageOrg<Address>


        printDebug("\thouse findBy_ID OrgUuid = ${tokenObjUuid} houseId = $houseId")
        house = houseDao.findByHouse_Id(tokenObjUuid, houseId) ?: throw NotFoundException("ไม่พบข้อมูลบ้านที่ระบุ")


        printDebug("\t\t$house")
        val feture = createGeo(house.data, tokenObjUuid)
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



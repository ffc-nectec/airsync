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

import ffc.airsync.api.dao.DaoFactory
import ffc.model.*
import me.piruin.geok.geometry.Feature
import me.piruin.geok.geometry.FeatureCollection
import java.util.*
import javax.ws.rs.NotFoundException
import kotlin.collections.ArrayList

object HouseService {

    val houseDao = DaoFactory().buildHouseDao()

    fun create(token: String, orgId: String, houseList: List<Address>) {
        val org = getOrgByOrgToken(token, orgId)
        houseDao.insert(org.uuid, houseList)
    }

    fun update(token: String, orgId: String, houseList: List<Address>) {
        val org = getOrgByMobileToken(token = UUID.fromString(token), orgId = orgId)
        //val orgToken = getOrgByMobileToken(token = UUID.fromString(token), orgId = orgId)


    }

    fun get(token: String, orgId: String, page: Int = 1, per_page: Int = 200, hid: Int = -1): FeatureCollection {


        printDebug("Token = $token")

        val tokenObj = getOrgByMobileToken(UUID.fromString(token.trim()), orgId)

        printDebug("Befor check token")
        printDebug(tokenObj)


        printDebug("Search house match")

        val houseList: List<StorageOrg<Address>>

        if (hid > 0) {
            val house = houseDao.findByHouseId(tokenObj.uuid, hid)
              ?: throw NotFoundException("ไม่พบ hid บ้านให้ Update")
            houseList = ArrayList()
            houseList.add(house)
        } else {
            houseList = houseDao.find(tokenObj.uuid)
        }


        printDebug("count house = ${houseList.count()}")

        val geoJson = FeatureCollection()


        //val peopleInHouse=HashMap<String,ArrayList<People>>()


        val fromItem = (page - 1) * per_page
        var toItem = (page) * per_page
        val count = houseList.count()

        if (fromItem > count)
            throw NotFoundException("Query เกินหน้าสุดท้ายของบ้านแล้ว")

        if (toItem > count)
            toItem = count

        printDebug("page $page per_page $per_page")
        printDebug("from $fromItem to $toItem")


        (fromItem..toItem).forEach {
            //printDebug("Loop count $it")
            val data = houseList[it]

            val geometry = MyGeo("Point", data.data.latlng!!)
            //printDebug(geometry)
            val properits = ProperitsGeoJson(data.data.hid)
            //printDebug(properits)
            val houseId = data.data.hid
            //printDebug(houseId)
            val house = data.data
            //printDebug(house)

            properits.identity = data.data.identity
            properits.haveChronics = chronicDao.houseIsChronic(tokenObj.uuid, houseId!!)
            properits.no = house.no
            properits.road = house.road
            properits.coordinates = house.latlng
            properits.hid = house.hid



            try {
                properits.people = personDao.getPeopleInHouse(tokenObj.uuid, houseId)
            } catch (ex: Exception) {
                //printDebug("People null")
            }


            printDebug("Befor add feture")
            val feture: Feature<ProperitsGeoJson> = Feature(geometry, properties = properits)

            geoJson.features.add(feture)

        }


        //printDebug("For each house")
        /* houseList.forEach {




         }*/

        return geoJson


    }
}

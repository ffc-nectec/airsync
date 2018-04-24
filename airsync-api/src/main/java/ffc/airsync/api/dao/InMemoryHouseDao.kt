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

package ffc.airsync.api.dao

import ffc.model.Address
import ffc.model.StorageOrg
import ffc.model.printDebug
import java.util.*
import javax.ws.rs.NotFoundException

class InMemoryHouseDao : HouseDao {

    private constructor()

    companion object {
        val instant = InMemoryHouseDao()
    }


    val houseList = arrayListOf<StorageOrg<Address>>()


    override fun insert(orgUuid: UUID, house: Address) {
        //houseList.removeIf { it.uuid == orgUuid && it.data.identity?.id == house.identity?.id }
        printDebug("Insert house = ${house.identity?.id} XY= ${house.latlng}")
        houseList.add(StorageOrg(orgUuid, house))
    }

    override fun insert(orgUuid: UUID, houseList: List<Address>) {
        houseList.forEach {
            insert(orgUuid, it)
        }

    }

    override fun update(orgUuid: UUID, house: Address) {
        printDebug("Update house = ${house.identity?.id} XY= ${house.latlng}")
        val houseUpdate = houseList.find {
            it.uuid == orgUuid && it.data.hid == house.hid
        } ?: throw NotFoundException("ไม่มีรายการบ้านให้ Update")
        houseUpdate.data = house

    }

    override fun update(orgUuid: UUID, houseList: List<Address>) {
        houseList.forEach {
            update(orgUuid, it)
        }

    }

    override fun find(latlng: Boolean): List<StorageOrg<Address>> {
        if (latlng)
            return houseList.filter { it.data.latlng!!.latitude != 0.0 || it.data.latlng!!.longitude != 0.0 }
        else
            return houseList
    }

    override fun findByHouseId(orgUuid: UUID, houseId: Int): StorageOrg<Address>? {

        return houseList.find { it.data.hid == houseId && it.uuid == orgUuid }
    }

    override fun find(orgUuid: UUID, latlng: Boolean): List<StorageOrg<Address>> {
        if (latlng)
            return houseList.filter {
                (it.data.latlng!!.latitude != 0.0 || it.data.latlng!!.longitude != 0.0) && it.uuid == orgUuid
            }
        else
            return houseList.filter {
                it.uuid == orgUuid
            }
    }


    override fun removeByOrgUuid(orgUuid: UUID) {
        houseList.removeIf { it.uuid == orgUuid }
    }
}

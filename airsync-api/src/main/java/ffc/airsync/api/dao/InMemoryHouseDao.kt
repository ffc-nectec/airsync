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
import java.util.*

class InMemoryHouseDao : HouseDao {

    private constructor()

    companion object {
        val instant = InMemoryHouseDao()
    }


    val houseList = arrayListOf<StorageOrg<Address>>()


    override fun insert(orgUuid: UUID, house: Address) {
        //houseList.removeIf { it.uuid == orgUuid && it.data.identity?.id == house.identity?.id }
        println("Insert house = ${house.identity?.id} XY= ${house.latlng}")
        houseList.add(StorageOrg(orgUuid, house))
    }

    override fun insert(orgUuid: UUID, houseList: List<Address>) {
        houseList.forEach {
            insert(orgUuid, it)
        }

    }

    override fun find(latlng: Boolean): List<StorageOrg<Address>> {
        if (latlng)
            return houseList.filter { it.data.latlng!!.latitude != 0.0 || it.data.latlng!!.longitude != 0.0 }
        else
            return houseList
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


    override fun remove(orgUuid: UUID) {
        houseList.removeIf { it.uuid == orgUuid }
    }
}

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

import ffc.model.HouseOrg
import java.util.*

class InMemoryHouseDao :HouseDao {

    private constructor()

    companion object {
        val instant = InMemoryHouseDao()
    }



    val houseList = arrayListOf<HouseOrg>()



    override fun insert(orgUuid: UUID, house: HouseOrg) {
        house.orgUUID=orgUuid
        houseList.add(house)
    }

    override fun insert(orgUuid: UUID, houseList: List<HouseOrg>) {
        houseList.forEach {
            insert(orgUuid,it)
        }

    }

    override fun find(orgUuid: UUID) : List<HouseOrg>{
        return houseList.filter {
            it.orgUUID==orgUuid
        }
    }

    override fun findByHid(orgUuid: UUID, hid: Int) : List<HouseOrg>{
        return houseList.filter { it.houseId==hid &&(it.orgUUID==orgUuid)  }
    }

    override fun remove(orgUuid: UUID) {
        houseList.removeIf { it.orgUUID==orgUuid }
    }
}

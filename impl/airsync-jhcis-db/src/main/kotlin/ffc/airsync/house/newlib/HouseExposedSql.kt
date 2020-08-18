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

package ffc.airsync.house.newlib

import ffc.airsync.house.newlib.HouseSqlObject.hcode
import ffc.airsync.house.newlib.HouseSqlObject.hno
import ffc.airsync.house.newlib.HouseSqlObject.pcucode
import ffc.airsync.house.newlib.HouseSqlObject.pcucodepersonvola
import ffc.airsync.house.newlib.HouseSqlObject.pidvola
import ffc.airsync.house.newlib.HouseSqlObject.villcode
import ffc.airsync.house.newlib.HouseSqlObject.xgis
import ffc.airsync.house.newlib.HouseSqlObject.ygis
import ffc.entity.Link
import ffc.entity.System.JHICS
import ffc.entity.place.House
import ffc.entity.update
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class HouseExposedSql(connection: () -> Connection) {
    init {
        Database.connect(connection)
    }

    fun getAllHouse(): List<House> {
        val output = arrayListOf<House>()
        val util = Util()
        transaction {
            val houseList = HouseSqlObject.selectAll().map {
                val house = House().update(it[HouseSqlObject.dateupdate]) {
                    no = it[hno]
                    road = it[HouseSqlObject.road]
                    location = util.getLocation(util.jhcisGeoToDouble(it[xgis]), util.jhcisGeoToDouble(it[ygis]))
                    link = Link(
                        JHICS,
                        "hcode" to it[hcode],
                        "pcucode" to (it[pcucode] ?: ""),
                        "villcode" to it[villcode],
                        "pcucodepersonvola" to (it[pcucodepersonvola] ?: ""),
                        "pidvola" to (it[pidvola] ?: "")
                    )
                }
                house
            }
            output.addAll(houseList)
        }
        return output
    }
}

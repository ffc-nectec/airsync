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

package ffc.airsync.db

import ffc.airsync.utils.printDebug
import ffc.entity.House
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.ThaiHouseholdId
import ffc.entity.gson.toJson
import me.piruin.geok.geometry.Point
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.joda.time.DateTime
import java.sql.ResultSet

interface QueryHouse {

    @SqlQuery("""
SELECT house.pcucode,
	house.hcode,
    house.hno,
	house.road,
	house.xgis,
    house.hid,
	house.ygis,
	house.dateupdate
FROM house
""")
    @RegisterRowMapper(HouseMapper::class)
    fun getHouse(): List<House>
}

class HouseMapper : RowMapper<House> {

    override fun map(rs: ResultSet, ctx: StatementContext?): House {
        val timestamp = DateTime(rs.getTimestamp("dateupdate"))
        val house = House().update<House>(timestamp) {
            rs.getString("hid")?.let { identity = ThaiHouseholdId(it) }
            no = rs.getString("hno")
            road = rs.getString("road")
            location = Point(
                    rs.getDouble("ygis"),
                    rs.getDouble("xgis")
            )
            link = Link(System.JHICS,
                    "hcode" to rs.getString("hcode"),
                    "pcuCode" to rs.getString("pcucode")
            )
        }
        printDebug("Read house database" + house.toJson())
        return house
    }
}


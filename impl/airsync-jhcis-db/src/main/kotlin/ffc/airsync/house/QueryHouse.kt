/*
 * Copyright (c) 2018 NECTEC
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

package ffc.airsync.house

import ffc.airsync.utils.printDebug
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.ThaiHouseholdId
import ffc.entity.gson.toJson
import ffc.entity.place.House
import ffc.entity.update
import me.piruin.geok.geometry.Point
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.Define
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.joda.time.DateTime
import java.sql.ResultSet
import java.sql.Timestamp

interface QueryHouse {
    @SqlUpdate(
        """
UPDATE `house`
  SET
   `hid`= :hid,
   `road`= :road,
   `xgis`= :xgis,
   `ygis`= :ygis,
   `hno`= :hno,
   `dateupdate`= :dateUpdate
WHERE  `pcucode`= :pcucode AND `hcode`= :hcode
    """
    )
    fun update(@BindBean house: HouseJhcisDb)

    @SqlQuery(
        """
SELECT house.pcucode,
	house.hcode,
    house.hno,
    house.villcode,
	house.road,
	house.xgis,
    house.hid,
	house.ygis,
	house.dateupdate
FROM house
"""
    )
    @RegisterRowMapper(HouseMapper::class)
    fun findThat(): List<House>

    @SqlQuery(
        """
SELECT house.pcucode,
	house.hcode,
    house.hno,
    house.villcode,
	house.road,
	house.xgis,
    house.hid,
	house.ygis,
	house.dateupdate
FROM house WHERE <where>
"""
    )
    @RegisterRowMapper(HouseMapper::class)
    fun findThat(@Define("where") whereString: String): List<House>
}

class HouseMapper : RowMapper<House> {

    override fun map(rs: ResultSet, ctx: StatementContext?): House {
        val timestamp = DateTime(rs.getTimestamp("dateupdate")).minusHours(7)
        val regexMoo = Regex("""^\d+(\d{2})${'$'}""")
        val house = House().update(timestamp) {
            rs.getString("hid")?.let { identity = ThaiHouseholdId(it) }
            // val moo = regexMoo.matchEntire(rs.getString("villcode") ?: "00")?.groupValues?.last()?.toInt()
            no = rs.getString("hno")
            road = rs.getString("road")

            val xgis = rs.getDouble("xgis")
            val ygis = rs.getDouble("ygis")
            if ((xgis != 0.0) && (ygis != 0.0))
                location = if (xgis < ygis)
                    Point(xgis, ygis)
                else
                    Point(ygis, xgis)
            link = Link(
                System.JHICS,
                "hcode" to rs.getString("hcode"),
                "pcucode" to rs.getString("pcucode"),
                "villcode" to rs.getString("villcode")
            )
        }
        printDebug("Read house database" + house.toJson())
        return house
    }
}

data class HouseJhcisDb(
    val hid: String?,
    val road: String?,
    val xgis: String?,
    val ygis: String?,
    val hno: String?,
    val dateUpdate: Timestamp,

    val pcucode: String,
    val hcode: Int
)

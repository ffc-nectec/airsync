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

package ffc.airsync.client.client.module.daojdbi

import ffc.model.Address
import ffc.model.Chronic
import ffc.model.Person
import me.piruin.geok.LatLng
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet


interface QueryHouse {
    @SqlQuery("""
SELECT house.pcucode,
	house.hcode,
	house.road,
	house.xgis,
	house.ygis
FROM house
""")
    @RegisterRowMapper(HouseMapper::class)
    fun getHouse(): List<Address>


}


class HouseMapper : RowMapper<Address> {
    override fun map(rs: ResultSet, ctx: StatementContext?): Address {


        val hcode = rs.getInt("hcode")
        val road = rs.getString("road")
        val xgis = rs.getDouble("xgis")
        val ygis = rs.getDouble("ygis")

        val house = Address()

        house.road = road

        //if (xgis != 0.0 && ygis != 0.0)
        house.latlng = LatLng(ygis, xgis)

        return house

    }
}


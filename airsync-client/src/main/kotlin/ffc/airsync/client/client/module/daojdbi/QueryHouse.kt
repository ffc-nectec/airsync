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

import ffc.model.HouseOrg
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
	house.ygis,
	p.hcode as chronichcode
FROM house
LEFT JOIN (SELECT person.hcode FROM person
	INNER JOIN personchronic ON
		person.pcucodeperson=personchronic.pcucodeperson AND person.pid=personchronic.pid
	GROUP BY person.hcode
	ORDER BY person.hcode) p
		ON
		p.hcode=house.hcode
""")
    @RegisterRowMapper(HouseMapper::class)
    fun getHouse() :List<HouseOrg>


    @SqlQuery("SELECT person.hcode FROM person " +
      "JOIN personchronic ON " +
      "person.pcucodeperson=personchronic.pcucodeperson AND person.pid=personchronic.pid " +
      "GROUP BY person.hcode " +
      "ORDER BY person.hcode" )
    fun getHouseChronic() : List<Int>
}


class HouseMapper : RowMapper<HouseOrg> {
    override fun map(rs: ResultSet?, ctx: StatementContext?): HouseOrg {

        if (rs == null) throw ClassNotFoundException()

        return HouseOrg(
          houseId = rs.getString("hcode"),
          road =rs.getString("road"),
          xgis = rs.getString("xgis"),
          ygis = rs.getString("ygis"),
          haveChronics = rs.getString("chronichcode")!=null

          )

    }
}


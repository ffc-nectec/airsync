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

package ffc.airsync.temple

import ffc.airsync.getLogger
import ffc.airsync.utils.getLocation
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.place.Religion
import ffc.entity.place.ReligiousPlace
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

private val logger by lazy { getLogger(QueryTemple::class) }

interface QueryTemple {

    @SqlQuery(
        """
SELECT
	villagetemple.pcucode,
	villagetemple.villcode,
	villagetemple.templeno,
	villagetemple.templename as name,
	villagetemple.address,
	creligion.religionname as religion,
	villagetemple.xgis,
	villagetemple.ygis
FROM
	villagetemple
LEFT JOIN creligion ON
	creligion.religioncode=villagetemple.religion
    """
    )
    fun get(): List<ReligiousPlace>
}

class TempleMapper : RowMapper<ReligiousPlace> {
    override fun map(rs: ResultSet, ctx: StatementContext): ReligiousPlace {
        return ReligiousPlace().apply {
            name = rs.getString("name")
            no = rs.getString("address")
            religion = Religion.byName(rs.getString("religion"))

            location = getLocation(rs)
            link = Link(System.JHICS)
            link?.keys?.put("pcucode", rs.getString("pcucode"))
            link?.keys?.put("villcode", rs.getString("villcode"))
            link?.keys?.put("templeno", rs.getString("templeno"))
        }
    }
}

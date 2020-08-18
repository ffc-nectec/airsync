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

package ffc.airsync.school

import ffc.airsync.getLogger
import ffc.airsync.utils.getLocation
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.place.Education
import ffc.entity.place.School
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

private val logger by lazy { getLogger(QuerySchool::class) }

interface QuerySchool {

    @SqlQuery(
        """
SELECT
	villageschool.pcucode,
	villageschool.villcode,
	villageschool.schoolno,
	villageschool.schoolname as name,
	cschoolclass.classname as maxclass,
	cschooldepend.schooldependname as depen,
	villageschool.address,
	villageschool.xgis,
	villageschool.ygis
FROM
	villageschool
LEFT JOIN cschoolclass ON
	cschoolclass.classcode=villageschool.maxclass
LEFT JOIN cschooldepend ON
	cschooldepend.schooldependcode=villageschool.depend
    """
    )
    @RegisterRowMapper(SchoolMapper::class)
    fun get(): List<School>
}

class SchoolMapper : RowMapper<School> {
    override fun map(rs: ResultSet, ctx: StatementContext): School {
        return School().apply {

            name = rs.getString("name")
            educationLevel = Education.byName(rs.getString("maxclass"))
            depend = rs.getString("depen")
            no = rs.getString("address")
            location = getLocation(rs)
            link = Link(System.JHICS)
            link?.keys?.put("pcucode", rs.getString("pcucode"))
            link?.keys?.put("villcode", rs.getString("villcode"))
            link?.keys?.put("schoolno", rs.getString("schoolno"))
        }
    }
}

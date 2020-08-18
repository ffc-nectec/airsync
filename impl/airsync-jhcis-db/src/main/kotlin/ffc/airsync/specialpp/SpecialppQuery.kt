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

package ffc.airsync.specialpp

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.sql.ResultSet

private const val specialQuery = """
SELECT
	f43specialpp.ppspecial,
	f43specialpp.dateupdate,
    f43specialpp.visitno
FROM
	f43specialpp
"""

private const val visitNumberIndex = """CREATE  INDEX visitnumber ON f43specialpp(visitno)"""

interface SpecialppQuery {

    @SqlUpdate(visitNumberIndex)
    fun createIndex()

    @SqlQuery(
        specialQuery + """
    WHERE f43specialpp.visitno = :visitnumber
    """
    )
    @RegisterRowMapper(SpecialPPMapper::class)
    fun getBy(@Bind("visitnumber") visitnumber: Long): List<String>

    @SqlQuery(
        specialQuery + """
    WHERE f43specialpp.visitno IS NOT NULL
    """
    )
    @RegisterRowMapper(SpecialPPMapperAll::class)
    fun getAll(): List<HashMap<Long, String>>
}

class SpecialPPMapper : RowMapper<String> {
    override fun map(rs: ResultSet, ctx: StatementContext?): String {
        return rs.getString("ppspecial")
    }
}

class SpecialPPMapperAll : RowMapper<HashMap<Long, String>> {
    override fun map(rs: ResultSet, ctx: StatementContext?): HashMap<Long, String> {
        return hashMapOf(rs.getLong("visitno") to rs.getString("ppspecial")!!)
    }
}

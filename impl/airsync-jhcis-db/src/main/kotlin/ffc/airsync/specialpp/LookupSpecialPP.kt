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

import ffc.entity.healthcare.SpecialPP.PPType
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

private const val specialPPquery = """
SELECT
	cspecialpp.ppcode,
	cspecialpp.ppname
FROM
	cspecialpp

    """

interface LookupSpecialPP {
    @SqlQuery(specialPPquery + "WHERE cspecialpp.ppcode = :ppcode")
    @RegisterRowMapper(SpecialPpMapperType::class)
    fun get(@Bind("ppcode") ppcode: String): List<PPType>
}

internal class SpecialPpMapperType : RowMapper<PPType> {
    override fun map(rs: ResultSet, ctx: StatementContext?): PPType {
        return PPType(
            id = rs.getString("ppcode"),
            name = rs.getString("ppname")
        )
    }
}

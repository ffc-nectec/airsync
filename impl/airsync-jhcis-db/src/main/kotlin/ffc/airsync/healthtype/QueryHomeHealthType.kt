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

package ffc.airsync.healthtype

import ffc.entity.Lang
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.healthcare.CommunityService.ServiceType
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

private const val homeHealtyTypeQuery = """
SELECT
	chomehealthtype.homehealthcode as code,
	chomehealthtype.homehealthmeaning as mean,
	chomehealthtype.homehealthmap as map
FROM
	chomehealthtype

    """

interface QueryHomeHealthType {
    @SqlQuery(homeHealtyTypeQuery + "WHERE chomehealthtype.homehealthcode = :healthcode")
    @RegisterRowMapper(GetHomeHealthMapper::class)
    fun get(@Bind("healthcode") healthcode: String): List<ServiceType>
}

internal class GetHomeHealthMapper : RowMapper<ServiceType> {
    override fun map(rs: ResultSet?, ctx: StatementContext?): ServiceType {
        if (rs == null) throw ClassNotFoundException()

        return ServiceType(
            rs.getString("code"),
            rs.getString("mean")
        ).apply {

            translation[Lang.th] = rs.getString("mean")
            link = Link(System.JHICS).apply {
                keys["code"] = rs.getString("code")
                try {
                    keys["map"] = rs.getString("map")
                } catch (ignore: IllegalStateException) {
                }
            }
        }
    }
}

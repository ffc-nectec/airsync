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

import ffc.entity.Chronic
import ffc.entity.Link
import ffc.entity.System
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.joda.time.LocalDate
import java.sql.ResultSet

interface QueryChronic {
    @SqlQuery("""
        SELECT personchronic.pcucodeperson,person.hcode,personchronic.chroniccode,personchronic.datedxfirst,personchronic.pid
        FROM person
            JOIN personchronic
                ON person.pcucodeperson=personchronic.pcucodeperson
                AND person.pid=personchronic.pid
        ORDER BY person.hcode
            """)
    @RegisterRowMapper(ChronicMapper::class)
    fun getChronic(): List<Chronic>
}

class ChronicMapper : RowMapper<Chronic> {

    override fun map(rs: ResultSet?, ctx: StatementContext?): Chronic {

        if (rs == null) throw ClassNotFoundException()

        val hcode = rs.getInt("hcode")
        val hospCode = rs.getString("pcucodeperson")
        val pid = rs.getInt("pid")

        val chronic = Chronic(idc10 = rs.getString("chroniccode")).apply {
            diagDate = LocalDate.fromDateFields(rs.getDate("datedxfirst"))
            link = Link(System.JHICS,
                    "hcode" to "$hcode",
                    "pcucodeperson" to hospCode,
                    "pid" to "$pid")
        }

        return chronic

    }
}

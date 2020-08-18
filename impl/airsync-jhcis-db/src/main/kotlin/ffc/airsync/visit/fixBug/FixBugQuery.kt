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

package ffc.airsync.visit.fixBug

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlBatch
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.sql.ResultSet

internal interface FixBugQuery {

    @SqlUpdate("""DELETE FROM visit WHERE visitno=:visitno""")
    fun deleateVisit(@Bind("visitno") VisitNumber: Long)

    @SqlUpdate("""DELETE FROM visithomehealthindividual WHERE visitno=:visitno""")
    fun deleteVisitHomeHealthIndividual(@Bind("visitno") VisitNumber: Long)

    @SqlUpdate("""DELETE FROM visitdiag WHERE visitno=:visitno""")
    fun deleteVisitDiag(@Bind("visitno") VisitNumber: Long)

    @SqlUpdate("""DELETE FROM f43specialpp WHERE visitno=:visitno""")
    fun deleteF43SpecialPP(@Bind("visitno") VisitNumber: Long)

    @SqlUpdate("""DELETE FROM ncd_person_ncd_screen WHERE visitno=:visitno""")
    fun deleteNCDs(@Bind("visitno") VisitNumber: Long)

    @SqlQuery(
        "SELECT pcucode," +
                "visitdate," +
                "pid," +
                "pcucodeperson," +
                "timestart," +
                "timeend " +
                "FROM visit WHERE visitno = :visitno"
    )
    @RegisterRowMapper(VisitFixBugMapper::class)
    fun getVisitBy(@Bind("visitno") visitNumber: Long): List<VisitFixBug>

    @SqlBatch("""""")
    fun createVisitIndex()
}

internal class VisitFixBugMapper : RowMapper<VisitFixBug> {
    override fun map(rs: ResultSet, ctx: StatementContext?): VisitFixBug {
        return VisitFixBug(
            rs.getString("pcucode"),
            rs.getString("visitdate"),
            rs.getLong("pid"),
            rs.getString("pcucodeperson"),
            rs.getString("timestart"),
            rs.getString("timeend")
        )
    }
}

internal data class VisitFixBug(
    val pcuCode: String,
    val visitDate: String,
    val pid: Long,
    val pcuCodePerson: String?,
    val timeStart: String?,
    val timeEnd: String?
)

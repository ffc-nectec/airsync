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

package ffc.airsync.ncds

import ffc.airsync.getLogger
import ffc.airsync.utils.ignore
import ffc.airsync.utils.ignoreW
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.healthcare.BloodPressure
import ffc.entity.healthcare.Frequency
import ffc.entity.healthcare.NCDScreen
import ffc.entity.update
import ffc.entity.util.generateTempId
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.result.ResultSetException
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.joda.time.DateTime
import java.sql.ResultSet
import java.sql.SQLException

private val logger by lazy { getLogger(NCDscreenQuery::class) }
private const val ncdScreenQuery = """
SELECT
	ncd_person_ncd_screen.pcucode,
	ncd_person_ncd_screen.pid,
	ncd_person_ncd_screen.`no`,
	ncd_person_ncd_screen.screen_date,

	ncd_person_ncd_screen.height,
	ncd_person_ncd_screen.weight,
	ncd_person_ncd_screen.waist,
	ncd_person_ncd_screen.hbp_s1 as bloodPressureS1,
	ncd_person_ncd_screen.hbp_d1 as bloodPressureD1,
	ncd_person_ncd_screen.hbp_s2 as bloodPressureS2,
	ncd_person_ncd_screen.hbp_d2 as bloodPressureD2,
	ncd_person_ncd_screen.bsl as bloodSugar,
	ncd_person_ncd_screen.alcohol,
	ncd_person_ncd_screen.smoke,
	ncd_person_ncd_screen.screen_q1 as hasDmInFamily,
	ncd_person_ncd_screen.htfamily as hasHtInFamily,
	ncd_person_ncd_screen.user_update,
	ncd_person_ncd_screen.dateupdate,
    ncd_person_ncd_screen.visitno
FROM
	ncd_person_ncd_screen
"""

private const val visitNumberIndex = """CREATE INDEX visitnumber ON ncd_person_ncd_screen(visitno)"""

interface NCDscreenQuery {

    @SqlUpdate(visitNumberIndex)
    fun createIndex()

    @SqlQuery(
        ncdScreenQuery + """
        WHERE ncd_person_ncd_screen.visitno = :visitnumber
    """
    )
    @RegisterRowMapper(NCDscreenMapper::class)
    fun getBy(@Bind("visitnumber") visitnumber: Long): List<NCDScreen>

    @SqlQuery(
        ncdScreenQuery + """
        WHERE ncd_person_ncd_screen.visitno IS NOT NULL
    """
    )
    @RegisterRowMapper(NCDscreenMapperAll::class)
    fun getAll(): List<HashMap<Long, NCDScreen>>
}

class NCDscreenMapper : RowMapper<NCDScreen> {
    override fun map(rs: ResultSet, ctx: StatementContext?): NCDScreen {
        return createNcd(rs)
    }
}

class NCDscreenMapperAll : RowMapper<HashMap<Long, NCDScreen>> {
    override fun map(rs: ResultSet, ctx: StatementContext?): HashMap<Long, NCDScreen> {
        return hashMapOf(rs.getLong("visitno") to createNcd(rs))
    }
}

private fun createNcd(rs: ResultSet): NCDScreen {
    val pid = rs.getString("pid") // Use for error.
    return NCDScreen(
        providerId = rs.getString("user_update"),
        patientId = "",
        id = generateTempId(),
        hasDmInFamily = when (rs.getString("hasDmInFamily")) {
            "1" -> true
            "0" -> false
            else -> null
        },
        hasHtInFamily = when (rs.getString("hasHtInFamily")) {
            "1" -> true
            "0" -> false
            else -> null
        },
        smoke = when (rs.getString("smoke")) {
            "1" -> Frequency.NEVER
            "2" -> Frequency.RARELY
            "3" -> Frequency.OCCASIONALLY
            "4" -> Frequency.USUALLY
            else -> Frequency.UNKNOWN
        },
        alcohol = when (rs.getString("alcohol")) {
            "1" -> Frequency.NEVER
            "2" -> Frequency.RARELY
            "3" -> Frequency.OCCASIONALLY
            "4" -> Frequency.USUALLY
            else -> Frequency.UNKNOWN
        },
        bloodSugar = ignore { rs.getDouble("bloodSugar") },
        weight = ignoreW(NCDscreenQuery::class) { rs.getString("weight")?.toDoubleOrNull() },
        height = ignoreW(NCDscreenQuery::class) { rs.getString("height")?.toDoubleOrNull() },
        waist = ignoreW(NCDscreenQuery::class) { rs.getString("waist")?.toDoubleOrNull() },
        bloodPressure = ignoreW(NCDscreenQuery::class) {
            rs.getString("bloodPressureS1")?.let {
                BloodPressure(it.toDouble(), rs.getString("bloodPressureD1").toDouble())
            }
        },
        bloodPressure2nd = ignoreW(NCDscreenQuery::class) {
            rs.getString("bloodPressureS2")?.let {
                BloodPressure(it.toDouble(), rs.getString("bloodPressureD2").toDouble())
            }
        }
    ).update(DateTime(rs.getTimestamp("dateupdate")).minusHours(7)) {

        try {
            rs.getDate("screen_date")?.let {
                time = DateTime(it)
            }
        } catch (ex: SQLException) {
            logger.error("screen_date ของ pid $pid ผิดไปจากรูปแบบปกติ ${ex.message}", ex)
        }

        link = Link(System.JHICS)
        rs.getString("pcucode")?.let { link!!.keys["pcucode"] = it }
        pid?.let { link!!.keys["pid"] = it }
        rs.getString("no")?.let { link!!.keys["no"] = it }
        try {
            rs.getString("screen_date")?.let { link!!.keys["screen_date"] = it }
        } catch (ignore: ResultSetException) {
        } catch (ignore: SQLException) {
        }
    }
}

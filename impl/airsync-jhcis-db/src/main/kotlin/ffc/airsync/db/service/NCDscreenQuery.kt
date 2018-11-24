package ffc.airsync.db.service

import ffc.entity.Link
import ffc.entity.System
import ffc.entity.healthcare.BloodPressure
import ffc.entity.healthcare.Frequency
import ffc.entity.healthcare.NCDScreen
import ffc.entity.util.generateTempId
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.joda.time.DateTime
import java.sql.ResultSet

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
	ncd_person_ncd_screen.dateupdate
FROM
	ncd_person_ncd_screen
"""

private const val visitNumberIndex = """CREATE INDEX visitnumber ON ncd_person_ncd_screen(visitno)"""

interface NCDscreenQuery {

    @SqlQuery(
        ncdScreenQuery + """
        WHERE ncd_person_ncd_screen.visitno = :visitnumber
    """
    )
    @RegisterRowMapper(NCDscreenMapper::class)
    fun get(@Bind("visitnumber") visitnumber: Int): List<NCDScreen>
}

class NCDscreenMapper : RowMapper<NCDScreen> {
    override fun map(rs: ResultSet, ctx: StatementContext?): NCDScreen {
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
            bloodSugar = rs.getDouble("bloodSugar"),
            weight = rs.getString("weight")?.toDouble(),
            height = rs.getString("height")?.toDouble(),
            waist = rs.getString("waist")?.toDouble(),
            bloodPressure = rs.getString("bloodPressureS1")?.let {
                BloodPressure(it.toDouble(), rs.getString("bloodPressureD1").toDouble())
            },
            bloodPressure2nd = rs.getString("bloodPressureS2")?.let {
                BloodPressure(it.toDouble(), rs.getString("bloodPressureD2").toDouble())
            }
        ).apply {

            rs.getDate("dateupdate")?.let {
                time = DateTime(it).minusHours(7)
            }

            link = Link(System.JHICS)
            rs.getString("pcucode")?.let { link!!.keys["pcucode"] = it }
            rs.getString("pid")?.let { link!!.keys["pid"] = it }
            rs.getString("no")?.let { link!!.keys["no"] = it }
            try {
                rs.getString("screen_date")?.let { link!!.keys["screen_date"] = it }
            } catch (ignore: org.jdbi.v3.core.result.ResultSetException) {
            } catch (ignore: java.sql.SQLException) {
            }
        }
    }
}

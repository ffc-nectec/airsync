package ffc.airsync.db.service

import ffc.entity.Link
import ffc.entity.System
import ffc.entity.healthcare.BloodPressure
import ffc.entity.healthcare.HealthCareService
import ffc.entity.util.generateTempId
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

private const val visitQuery = """
SELECT
	visit.pcucode,
	visit.visitno,
	visit.visitdate,
	visit.pcucodeperson,
	visit.pid,
	visit.timeservice,
	visit.timestart,
	visit.timeend,
	visit.symptoms,
	visit.vitalcheck,
	visit.weight,
	visit.height,
	visit.pressure,
	visit.pressure2,
	visit.pressurelevel,
	visit.temperature,
	visit.pulse,
	visit.respri,
	visit.username,
	visit.flagservice,
	visit.dateupdate,
	visit.bmilevel,
	visit.flag18fileexpo,
	visit.rightcode,
	visit.rightno,
	visit.hosmain,
	visit.hossub,
	visit.waist,
	visit.ass,
	visit.healthsuggest1 as suggest,
	visit.diagnote
FROM
	visit
"""

interface VisitQuery {
    @SqlQuery(visitQuery)
    @RegisterRowMapper(VisitMapper::class)
    fun get(): List<HealthCareService>

    @SqlQuery(
        """
        SELECT visitno FROM visit WHERE visit.visitno = (SELECT MAX(visit.visitno) FROM visit) LIMIT 1
    """
    )
    @RegisterRowMapper(MaxVisitNumberMapper::class)
    fun getMaxVisitNumber(): List<Long>
}

class VisitMapper : RowMapper<HealthCareService> {
    override fun map(rs: ResultSet, ctx: StatementContext?): HealthCareService {
        return HealthCareService(
            providerId = rs.getString("username"),
            patientId = rs.getString("pid"),
            id = generateTempId()
        ).apply {

            syntom = rs.getString("symptoms")

            rs.getString("suggest")?.let { suggestion = it }
            rs.getString("weight")?.let { weight = it.toDouble() }
            rs.getString("height")?.let { height = it.toDouble() }
            rs.getString("waist")?.let { waist = it.toDouble() }
            rs.getString("ass")?.let { ass = it.toDouble() }

            rs.getString("pressure")?.let {
                bloodPressure = it.getBloodPressure()
            }

            rs.getString("pressure2")?.let {
                bloodPressure2nd = it.getBloodPressure()
            }

            rs.getString("pulse")?.let {
                pulseRate = it.toDouble()
            }

            rs.getString("temperature")?.let { bodyTemperature = it.toDouble() }

            rs.getString("diagnote")?.let { note = it }

            link = Link(System.JHICS)
            rs.getString("pcucode")?.let { link!!.keys["pcucode"] = it }
            rs.getString("visitno")?.let { link!!.keys["visitno"] = it }
            rs.getString("pid")?.let { link!!.keys["pid"] = it }
            rs.getString("rightcode")?.let { link!!.keys["rightcode"] = it }
            rs.getString("rightno")?.let { link!!.keys["rightno"] = it }
            rs.getString("hosmain")?.let { link!!.keys["hosmain"] = it }
            rs.getString("hossub")?.let { link!!.keys["hossub"] = it }
        }
    }
}

private fun String.getBloodPressure(): BloodPressure? = this.getSystolic()?.let { systolic ->
    this.getDiastolic()?.let { diastolic ->
        BloodPressure(systolic, diastolic)
    }
}

private fun String.getSystolic(): Double? = Regex("""(\d+)/\d+""").matchEntire(this)?.groupValues?.last()?.toDouble()

private fun String.getDiastolic(): Double? = Regex("""\d+/(\d)+""").matchEntire(this)?.groupValues?.last()?.toDouble()

class MaxVisitNumberMapper : RowMapper<Long> {

    override fun map(rs: ResultSet?, ctx: StatementContext?): Long {
        if (rs == null) throw NullPointerException("MaxVisitNumberMapper result set is null")
        return rs.getLong("visitno")
    }
}

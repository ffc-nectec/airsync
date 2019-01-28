package ffc.airsync.visit

import ffc.airsync.utils.printDebug
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.healthcare.BloodPressure
import ffc.entity.healthcare.HealthCareService
import ffc.entity.update
import ffc.entity.util.generateTempId
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.Define
import org.jdbi.v3.sqlobject.statement.SqlBatch
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.joda.time.DateTime
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
private const val insertVisit = """
INSERT INTO `jhcisdb`.`visit`
    (`pcucode`,
	`visitno`,
	`visitdate`,
	`pcucodeperson`,
	`pid`,
	`timeservice`,
	`timestart`,
	`timeend`,
	`symptoms`,
	`vitalcheck`,
	`weight`,
	`height`,
	`pressure`,
    `pressure2`,
	`pressurelevel`,
	`temperature`,
	`pulse`,
	`respri`,
    `username`,
    `flagservice`,
    `dateupdate`,
    `bmilevel`,
    `flag18fileexpo`,
    `rightcode`,
    `rightno`,
    `hosmain`,
    `hossub`,
    `waist`,
    `ass`)
VALUES
    (
    :pcucode,
	:visitno,
	:visitdate,
	:pcucodeperson,
	:pid,
	:timeservice,
	:timestart,
	:timeend,
	:symptoms,
	:vitalcheck,
	:weight,
	:height,
	:pressure,
	:pressure2,
	:pressurelevel,
	:temperature,
	:pulse,
	:respri,
    :username,
    :flagservice,
    :dateupdate,
    :bmilevel,
    :flag18fileexpo,
    :rightcode,
    :rightno,
    :hosmain,
    :hossub,
    :waist,
    :ass
    )
    """
private const val updateVisit = """
UPDATE `jhcisdb`.`visit` SET
	`timeservice`= :timeservice,
	`timestart`= :timestart,
	`timeend`= :timeend,
	`symptoms`= :symptoms,
	`vitalcheck`= :vitalcheck,
	`weight`= :weight,
	`height`= :height,
	`pressure`= :pressure,
	`pressure2`= :pressure2,
	`pressurelevel`= :pressurelevel,
	`temperature`= :temperature,
	`pulse`= :pulse,
	`respri`= :respri,
    `username`= :username,
    `flagservice`= :flagservice,
    `dateupdate`= :dateupdate,
    `bmilevel`= :bmilevel,
    `flag18fileexpo`= :flag18fileexpo,
    `rightcode`= :rightcode,
    `rightno`= :rightno,
    `hosmain`= :hosmain,
    `hossub`= :hossub,
    `waist`= :waist,
    `ass`= :ass
WHERE
	`pcucode`= :pcucode AND `visitno`= :visitno
"""

private const val visitQueryLast1Year = visitQuery + """
    WHERE visit.dateupdate >= NOW() - INTERVAL 1 YEAR
        AND
    visit.timestart IS NOT NULL
		AND
	visit.timeend IS NOT NULL
"""

private const val visitQueryWhere = visitQuery + """
    WHERE <where>
"""

private const val visitNumberIndex = """CREATE  INDEX visitnumber ON f43specialpp(visitno)"""

interface VisitQuery {

    @SqlUpdate(visitNumberIndex)
    fun createIndex()

    @SqlQuery(visitQueryLast1Year)
    @RegisterRowMapper(VisitMapper::class)
    fun get(): List<HealthCareService>

    @SqlQuery(visitQueryWhere)
    @RegisterRowMapper(VisitMapper::class)
    fun get(@Define("where") whereString: String): List<HealthCareService>

    @SqlQuery(
        """
        SELECT visitno FROM visit WHERE visit.visitno = (SELECT MAX(visit.visitno) FROM visit) LIMIT 1
    """
    )
    @RegisterRowMapper(MaxVisitNumberMapper::class)
    fun getMaxVisitNumber(): List<Long>

    @SqlBatch(insertVisit)
    fun insertVisit(@BindBean homeInsert: List<InsertData>)

    @SqlUpdate(
        """
        INSERT INTO `jhcisdb`.`visit` (`pcucode`, `visitno`) VALUES ( :pcuCode, :visitNumber )
    """
    )
    fun inserVisit(
        @Bind("pcuCode") pcuCode: String,
        @Bind("visitNumber") visitNumber: Long
    )

    @SqlUpdate(updateVisit)
    fun updateVisit(@BindBean homeInsert: InsertData)
}

class VisitMapper : RowMapper<HealthCareService> {
    override fun map(rs: ResultSet, ctx: StatementContext?): HealthCareService {
        return HealthCareService(
            providerId = rs.getString("username"),
            patientId = rs.getString("pid"),
            id = generateTempId()
        ).update(DateTime(rs.getTimestamp("dateupdate")).minusHours(7)) {

            syntom = rs.getString("symptoms")

            val visitdate = rs.getDate("visitdate")

            rs.getTime("timestart").let { timestart ->
                if (timestart != null)
                    time = DateTime(visitdate).plus(timestart.time).minusHours(7)
                else {
                    printDebug("Visit timestart is null visitno:${rs.getString("visitno")}")
                }

                rs.getTime("timeend")?.let { timeend ->
                    try {
                        endTime = DateTime(visitdate).plus(timeend.time).minusHours(7)
                    } catch (ex: java.lang.IllegalArgumentException) {
                        printDebug(
                            "Visit time end error " +
                                    "time=$time " +
                                    "endtime=${DateTime(visitdate).plus(timeend.time).minusHours(7)} ${ex.message}"
                        )
                    }
                }
            }

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
            link!!.isSynced = true
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

private fun String.getDiastolic(): Double? = Regex("""\d+/(\d+)""").matchEntire(this)?.groupValues?.last()?.toDouble()

class MaxVisitNumberMapper : RowMapper<Long> {

    override fun map(rs: ResultSet?, ctx: StatementContext?): Long {
        if (rs == null) throw NullPointerException("MaxVisitNumberMapper result set is null")
        return rs.getLong("visitno")
    }
}

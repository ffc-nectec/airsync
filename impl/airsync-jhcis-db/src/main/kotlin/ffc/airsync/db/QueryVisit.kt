package ffc.airsync.db

import ffc.airsync.utils.toTime
import ffc.entity.healthcare.HomeVisit
import ffc.entity.healthcare.bloodPressureLevel
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlBatch
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.joda.time.DateTime
import java.sql.ResultSet
import java.sql.Time
import java.sql.Timestamp

interface QueryVisit {

    @SqlQuery(
        """
        SELECT visitno FROM visit WHERE visit.visitno = (SELECT MAX(visit.visitno) FROM visit) LIMIT 1
    """
    )
    @RegisterRowMapper(MaxVisitNumberMapper::class)
    fun getMaxVisitNumber(): List<Long>

    @SqlUpdate(
        """
        INSERT INTO `jhcisdb`.`visit` (`pcucode`, `visitno`) VALUES ( :pcuCode, :visitNumber )
    """
    )
    fun inserVisit(
        @Bind("pcuCode") pcuCode: String,
        @Bind("visitNumber") visitNumber: Long
    )

    @SqlBatch(
        """
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
	`pressurelevel`,
	`temperature`,
	`pulse`,
	`respri`,
    `username`,
    `flagservice`,
    `dateupdate`)
VALUES
    (:pcucode,
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
	:pressurelevel,
	:temperature,
	:pulse,
	:respri,
    :username,
    :flagservice,
    :dateupdate)
    """
    )
    fun insertVisit(@BindBean homeVisit: List<VisitData>)

    @SqlUpdate(
        """
UPDATE `jhcisdb`.`visit`
	SET
		`visitdate` = :visitdate,
		`pcucodeperson` = :pcucodeperson,
		`pid` = :pid,
		`timeservice` = :timeservice,
		`timestart` = :timestart,
		`timeend` = :timeend,
		`symptoms` = :symptoms,
		`vitalcheck` = :vitalcheck,
		`weight` = :weight,
		`height` = :height,
		`pressure` = :pressure,
		`pressurelevel` = :pressurelevel,
		`temperature` = :temperature,
		`pulse` = :pulse,
		`respri` = :respri
	WHERE
		`pcucode`= :pcucode AND `visitno`= :visitno
    """
    )

    fun updateVisit(
        @BindBean homeVisit: List<VisitData>
    )
}

class MaxVisitNumberMapper : RowMapper<Long> {
    override fun map(rs: ResultSet?, ctx: StatementContext?): Long {
        if (rs == null) throw NullPointerException("MaxVisitNumberMapper result set is null")
        return rs.getLong("visitno")
    }
}

class VisitData(
    he: HomeVisit,
    val pcucode: String,
    val visitno: Long,
    val pcucodeperson: String,
    val pid: Long,
    val username: String

) {

    val flagservice = "03"
    val dateupdate: Timestamp = Timestamp(DateTime.now().millis)

    val visitdate: Timestamp = Timestamp(he.time.millis)
    val timestart: Time = Time(he.time.millis).toTime()
    val timeend = Time(he.time.plusMinutes(5).millis).toTime()
    val symptoms = he.syntom
    val vitalcheck = he.result
    val weight = he.weight
    val height = he.height

    val bmilevel = when {
        he.bmi == null -> null
        he.bmi!!.isOverweight -> "5"
        he.bmi!!.isObese -> "5"
        he.bmi!!.isNormal -> "3"
        else -> "1"
    }

    val bpLevel = he.bloodPressureLevel
    val bp = he.bloodPressure
    val pressure = if (bp != null) "${bp.systolic.toInt()}/${bp.diastolic.toInt()}" else null
    val pressurelevel = when {
        bpLevel == null -> null
        bpLevel.isHigh -> "3"
        bpLevel.isPreHigh -> "2"
        else -> "1"
    }
    val temperature = he.bodyTemperature

    val pulse = if (he.pulseRate == null) null else he.pulseRate
    val respri = if (he.respiratoryRate == null) null else he.respiratoryRate

    val timeservice: Int
        get() {
            return when {
                timestart > "08:30:00".toTime() && timestart < "16:30:00".toTime() -> 1
                else -> 2
            }
        }
}

package ffc.airsync.db

import ffc.airsync.utils.toTime
import ffc.entity.healthcare.Diagnosis
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
    `dateupdate`,
    `bmilevel`,
    `flag18fileexpo`)
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
    :dateupdate,
    :bmilevel,
    :flag18fileexpo)
    """
    )
    fun insertVisit(@BindBean homeVisit: List<VisitData>)

    @SqlBatch(
        """
INSERT INTO `jhcisdb`.`visitdiag` (
	`pcucode`,
	`visitno`,
	`diagcode`,
	`conti`,
	`dxtype`,
	`appointdate`,
	`dateupdate`,
	`doctordiag`)
VALUES(
	:pcucode ,
	:visitno ,
	:diagcode ,
	:conti ,
	:dxtype ,
	:appointdate ,
	:dateupdate ,
	:doctordiag )
    """
    )
    fun insertVisitDiag(@BindBean visitDiagData: VisitDiagData)

    @SqlUpdate(
        """
INSERT INTO `jhcisdb`.`visithomehealthindividual` (
	`pcucode`,
	`visitno`,
	`homehealthtype`,
	`patientsign`,
	`homehealthdetail`,
	`homehealthresult`,
	`homehealthplan`,
	`dateappoint`,
	`user`,
	`dateupdate`)
VALUES(
	:pcucode ,
	:visitno ,
	:homehealthtype ,
	:patientsign ,
	:homehealthdetail ,
	:homehealthresult ,
	:homehealthplan ,
	:dateappoint ,
	:user ,
	:dateupdate)
    """
    )
    fun insertVitsitIndividual(@BindBean visitIndividualData: VisitIndividualData)

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
    homeVisit: HomeVisit,
    val pcucode: String,
    val visitno: Long,
    val pcucodeperson: String,
    val pid: Long,
    val username: String

) {

    val flagservice = "03"
    val dateupdate: Timestamp = Timestamp(DateTime.now().millis)

    val visitdate: Timestamp = Timestamp(homeVisit.time.millis)
    val timestart: Time = Time(homeVisit.time.millis).toTime()
    val timeend = Time(homeVisit.time.plusMinutes(5).millis).toTime()
    val symptoms = homeVisit.syntom
    val vitalcheck = homeVisit.result
    val weight = homeVisit.weight
    val height = homeVisit.height

    val bmilevel = when {
        homeVisit.bmi == null -> null
        homeVisit.bmi!!.isOverweight -> "5"
        homeVisit.bmi!!.isObese -> "5"
        homeVisit.bmi!!.isNormal -> "3"
        else -> "1"
    }

    val bpLevel = homeVisit.bloodPressureLevel
    val bp = homeVisit.bloodPressure
    val pressure = if (bp != null) "${bp.systolic.toInt()}/${bp.diastolic.toInt()}" else null
    val pressurelevel = when {
        bpLevel == null -> null
        bpLevel.isHigh -> "3"
        bpLevel.isPreHigh -> "2"
        else -> "1"
    }
    val temperature = homeVisit.bodyTemperature
    val flag18fileexpo = "2"
    val pulse = if (homeVisit.pulseRate == null) null else homeVisit.pulseRate
    val respri = if (homeVisit.respiratoryRate == null) null else homeVisit.respiratoryRate

    val timeservice: Int
        get() {
            return when {
                timestart > "08:30:00".toTime() && timestart < "16:30:00".toTime() -> 1
                else -> 2
            }
        }
}

class VisitDiagData(
    homeVisit: HomeVisit,
    val pcucode: String,
    val visitno: Long,
    val username: String
) {

    val diagcode = arrayListOf<String>()
    val conti = arrayListOf<String>()
    val dxtype = arrayListOf<String>()
    val dateupdate: Timestamp = Timestamp(DateTime.now().millis)
    val doctordiag = username
    val appointdate =
        if (homeVisit.nextAppoint != null)
            Timestamp(homeVisit.nextAppoint!!.toDate().time)
        else
            null

    init {
        homeVisit.diagnosises.forEach {
            val icd10 = it.disease.icd10!!
            val continune = if (it.isContinued) "1" else "0"
            val diagnosis = when (it.dxType) {
                Diagnosis.Type.PRINCIPLE_DX -> "01"
                Diagnosis.Type.CO_MORBIDITY -> "02"
                Diagnosis.Type.COMPLICATION -> "03"
                Diagnosis.Type.OTHER -> "04"
                else -> "05"
            }

            diagcode.add(icd10)
            conti.add(continune)
            dxtype.add(diagnosis)
        }
    }
}

class VisitIndividualData(
    homeVisit: HomeVisit,
    val pcucode: String,
    val visitno: Long,
    val username: String,
    val homehealthtype: String
) {

    val patientsign = homeVisit.syntom
    val homehealthdetail = homeVisit.detail
    val homehealthresult = homeVisit.result
    val homehealthplan = homeVisit.plan
    val dateappoint =
        if (homeVisit.nextAppoint != null)
            Timestamp(homeVisit.nextAppoint!!.toDate().time)
        else
            null

    val user = username
    val dateupdate = Timestamp(DateTime.now().millis)
}

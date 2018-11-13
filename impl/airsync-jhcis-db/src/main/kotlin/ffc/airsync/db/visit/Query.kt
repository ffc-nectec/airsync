package ffc.airsync.db.visit

import ffc.entity.Link
import ffc.entity.System
import ffc.entity.healthcare.BloodPressure
import ffc.entity.healthcare.CommunityServiceType
import ffc.entity.healthcare.Diagnosis
import ffc.entity.healthcare.Disease
import ffc.entity.healthcare.HomeVisit
import ffc.entity.util.generateTempId
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.joda.time.DateTime
import org.joda.time.LocalDate
import java.sql.ResultSet

interface Query {
    @SqlQuery(
        """
        SELECT visitno FROM visit WHERE visit.visitno = (SELECT MAX(visit.visitno) FROM visit) LIMIT 1
    """
    )
    @RegisterRowMapper(MaxVisitNumberMapper::class)
    fun getMaxVisitNumber(): List<Long>

    @SqlQuery(
        """
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

	visitdiag.diagcode,
	visitdiag.conti,
	visitdiag.dxtype,
	visitdiag.appointdate,
	visitdiag.dateupdate,
	visitdiag.doctordiag,

	visithomehealthindividual.visitno,
	visithomehealthindividual.homehealthtype,
	visithomehealthindividual.patientsign,
	visithomehealthindividual.homehealthdetail,
	visithomehealthindividual.homehealthresult,
	visithomehealthindividual.homehealthplan,
	visithomehealthindividual.dateappoint,
	visithomehealthindividual.`user`,
	visithomehealthindividual.dateupdate

FROM
	visithomehealthindividual
JOIN visit ON
	visithomehealthindividual.visitno=visit.visitno AND
	visithomehealthindividual.pcucode=visit.pcucode
JOIN visitdiag ON
	visitdiag.visitno=visithomehealthindividual.visitno AND
	visitdiag.pcucode=visithomehealthindividual.pcucode
    """
    )
    @RegisterRowMapper(HomeVisitMapper::class)
    fun getHomeVisit(): List<HomeVisit>
}

class HomeVisitMapper : RowMapper<HomeVisit> {
    override fun map(rs: ResultSet, ctx: StatementContext): HomeVisit {
        return HomeVisit(
            providerId = rs.getString("doctordiag"),
            patientId = rs.getString("pid"),
            id = generateTempId(),

            serviceType = CommunityServiceType(
                id = rs.getString("homehealthtype"),
                name = ""
            )
        ).apply {

            // IndividualData
            syntom = rs.getString("patientsign")
            detail = rs.getString("homehealthdetail")
            result = rs.getString("homehealthresult")
            plan = rs.getString("homehealthplan")
            nextAppoint = LocalDate(rs.getDate("dateappoint").time)

            // DiagData
            diagnosises.add(
                Diagnosis(
                    disease = Disease(
                        id = generateTempId(),
                        name = "",
                        icd10 = rs.getString("diagcode")
                    ),
                    dxType = when (rs.getString("dxtype")) {
                        "01" -> Diagnosis.Type.PRINCIPLE_DX
                        "02" -> Diagnosis.Type.CO_MORBIDITY
                        "03" -> Diagnosis.Type.COMPLICATION
                        "04" -> Diagnosis.Type.OTHER
                        else -> Diagnosis.Type.EXTERNAL_CAUSE
                    },
                    isContinued = rs.getString("conti") == "1"
                )
            )

            // InsertData
            time = DateTime(rs.getDate("visitdate")).minusHours(7)
            if ((syntom ?: "").isBlank()) syntom = rs.getString("symptoms")
            if ((result ?: "").isBlank()) result = rs.getString("vitalcheck")
            weight = rs.getDouble("weight")
            height = rs.getDouble("height")
            waist = rs.getDouble("waist")
            ass = rs.getDouble("ass")

            val bp = rs.getString("pressure")
            val systolic = bp.getSystolic()
            val diastolic = bp.getDiastolic()
            if (systolic != null && diastolic != null)
                bloodPressure = BloodPressure(systolic, diastolic)

            bodyTemperature = rs.getDouble("temperature")
            pulseRate = rs.getDouble("pulse")
            respiratoryRate = rs.getDouble("respri")

            val link = Link(System.JHICS)
            link.keys = hashMapOf()
            link.keys["pcucode"] = rs.getString("pcucode")
            link.keys["visitno"] = rs.getString("visitno")
            link.keys["pid"] = rs.getString("pid")
            link.keys["rightcode"] = rs.getString("rightcode")
            link.keys["rightno"] = rs.getString("rightno")
            link.keys["hosmain"] = rs.getString("hosmain")
            link.keys["hossub"] = rs.getString("hossub")
            this.link = link
        }
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

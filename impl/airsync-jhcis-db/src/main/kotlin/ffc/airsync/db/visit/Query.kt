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
            rs.getString("patientsign")?.let { syntom = it }
            rs.getString("homehealthdetail")?.let { detail = it }
            rs.getString("homehealthresult")?.let { result = it }
            rs.getString("homehealthplan")?.let { plan = it }
            rs.getDate("dateappoint")?.let { nextAppoint = LocalDate(it.time) }

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
            rs.getString("weight")?.let { weight = it.toDouble() }
            rs.getString("height")?.let { height = it.toDouble() }
            rs.getString("waist")?.let { waist = it.toDouble() }
            rs.getString("ass")?.let { ass = it.toDouble() }

            rs.getString("pressure")?.let {
                val systolic = it.getSystolic()
                val diastolic = it.getDiastolic()
                if (systolic != null && diastolic != null)
                    bloodPressure = BloodPressure(systolic, diastolic)
            }

            rs.getString("temperature")?.let { bodyTemperature = it.toDouble() }
            rs.getString("pulse")?.let { pulseRate = it.toDouble() }
            rs.getString("respri")?.let { respiratoryRate = it.toDouble() }

            val link = Link(System.JHICS)
            link.keys = hashMapOf()
            rs.getString("pcucode")?.let { link.keys["pcucode"] = it }
            rs.getString("visitno")?.let { link.keys["visitno"] = it }
            rs.getString("pid")?.let { link.keys["pid"] = it }
            rs.getString("rightcode")?.let { link.keys["rightcode"] = it }
            rs.getString("rightno")?.let { link.keys["rightno"] = it }
            rs.getString("hosmain")?.let { link.keys["hosmain"] = it }
            rs.getString("hossub")?.let { link.keys["hossub"] = it }
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

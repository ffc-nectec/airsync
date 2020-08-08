package ffc.airsync.chronic

import ffc.airsync.Dao
import ffc.airsync.MySqlJdbi
import ffc.airsync.getLogger
import ffc.airsync.utils.ignoreW
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.healthcare.Chronic
import ffc.entity.healthcare.Icd10
import org.joda.time.LocalDate
import java.sql.ResultSet

class NewQueryChronic(private val jdbiDao: Dao = MySqlJdbi(null)) {
    private val logger = getLogger(this)

    interface Lookup {
        fun lookupDisease(icd10: String): Icd10
    }

    fun getBy(pcuCode: String, pid: String, lookup: () -> Lookup): List<Chronic> {
        return jdbiDao.instant.withHandle<List<Chronic>, Exception> { handle ->
            handle.createQuery(
                query + """
WHERE
    personchronic.pcucodeperson = :pcucode AND
    personchronic.pid = :pid
            """
            )
                .bind("pcucode", pcuCode)
                .bind("pid", pid)
                .map { rs, _ ->
                    mapData(rs, pid, lookup)
                }.list()
        }
    }

    private fun mapData(
        rs: ResultSet,
        pid: String,
        lookup: () -> Lookup
    ): Chronic {
        val hcode = rs.getInt("hcode")
        val hospCode = rs.getString("pcucodeperson")
        val diagDate = ignoreW(this) { LocalDate.fromDateFields(rs.getDate("datedxfirst")) }
        val icd10 = rs.getString("diseasecode")!!

        val link = Link(
            System.JHICS,
            "hcode" to "$hcode",
            "pcucodeperson" to hospCode,
            "pid" to pid
        )
        return Chronic(lookup().lookupDisease(icd10)).apply {
            this.link = link
            diagDate?.let { this.diagDate = diagDate }
        }
    }

    private val query = """
SELECT
	personchronic.pcucodeperson,
	person.hcode,
	personchronic.chroniccode,
	personchronic.datedxfirst,
	cdisease.diseasecode,
	cdisease.mapdisease,
	cdisease.diseasename,
	cdisease.diseasenamethai,
	cdisease.code504,
	cdisease.code506,
	cdisease.codechronic,
	cdisease.codeoccupa
FROM person
	JOIN personchronic
		ON person.pcucodeperson=personchronic.pcucodeperson
		AND person.pid=personchronic.pid
	INNER JOIN cdisease
		ON personchronic.chroniccode=cdisease.diseasecode
    """
}

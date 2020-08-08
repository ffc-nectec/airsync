package ffc.airsync.disability

import ffc.airsync.Dao
import ffc.airsync.MySqlJdbi
import ffc.airsync.getLogger
import ffc.entity.healthcare.Disability
import ffc.entity.healthcare.Icd10
import ffc.entity.healthcare.Severity
import ffc.entity.util.generateTempId
import org.joda.time.DateTime
import org.joda.time.LocalDate

class DisabilityJdbi(
    private val jdbiDao: Dao = MySqlJdbi(null)
) : DisabilityDao {
    private val logger = getLogger(this)

    init {
        logger.debug { "S0" }
    }

    override fun get(pcuCode: String, pid: String, lookupDisease: (icd10: String) -> Icd10?): List<Disability> {
        return jdbiDao.instant.withHandle<List<Disability>, Exception> { handle ->
            handle.createQuery(query)
                .bind("pcucode", pcuCode)
                .bind("pid", pid)
                .map { rs, _ ->

                    /**
                     * 1 การมองเห็น
                     * 2 การได้ยิน
                     * 3 ร่างกาย
                     * 4 ทางจิต พฤติกรรม
                     * 5 สติปัญญา
                     * 6 ทางการเรียนรู้
                     * 7 ออทิสติก
                     */
                    val group = rs.getString("incompletetype")?.preGroup()

                    // 1-9
                    val severity = rs.getString("unablelevel").preSeverity()
                    // วันที่พบ
                    val detectDate = DateTime(rs.getDate("datefound")).toLocalDate()
                    // สาเหตุความพิการ
                    val cause = rs.getString("disabcause").preCause()

                    val disease = rs.getString("diagcode")?.preDisease()

                    val start = DateTime(rs.getDate("datestartunable")).toLocalDate()

                    val disabilityRun = kotlin.runCatching {
                        Disability(
                            group!!,
                            detectDate ?: LocalDate.now(),
                            start ?: detectDate ?: LocalDate.now(),
                            cause,
                            disease,
                            severity
                        )
                    }
                    if (disabilityRun.isSuccess) {
                        disabilityRun.getOrNull()
                    } else {
                        null
                    }
                }.list().mapNotNull { it }
        }
    }

    private val query = """
SELECT
	personunable.pcucodeperson,
	personunable.pid,
	cpersonincomplete.incompletename,
	cpersonincomplete.incompletetype,
	personunable1type.unablelevel,
	personunable1type.datefound,
	personunable1type.disabcause,
	personunable1type.diagcode,
	personunable1type.datestartunable,
	personunable1type.dateupdate
FROM personunable
INNER JOIN personunable1type ON
	personunable.pcucodeperson=personunable1type.pcucodeperson
	AND
	personunable.pid=personunable1type.pid
INNER JOIN cpersonincomplete ON
	cpersonincomplete.incompletecode=personunable1type.typecode
WHERE
    personunable.pcucodeperson= :pcucode AND personunable.pid = :pid
    """

    private fun String?.preCause(): Disability.Cause {
        return when (this?.trim()) {
            "1" -> Disability.Cause.INBORN
            "2" -> Disability.Cause.INJURED
            "3" -> Disability.Cause.DISEASED
            else -> Disability.Cause.UNKNOWN
        }
    }

    private fun String.preDisease(): Icd10 {
        return Icd10(generateTempId(), this.trim())
    }

    private fun String?.preSeverity(): Severity {
        return when (this?.trim()) {
            "0", "1" -> Severity.OK
            "2", "3" -> Severity.LOW
            "4", "5" -> Severity.MID
            "6", "7" -> Severity.HI
            "8", "9" -> Severity.VERY_HI
            else -> Severity.UNDEFINED
        }
    }

    private fun String.preGroup(): Disability.Group? {
        return when (trim()) {
            "1" -> Disability.Group.BLINDNESS
            "2" -> Disability.Group.DEAFNESS
            "3" -> Disability.Group.MOBILITY
            "4" -> Disability.Group.MENTAL
            "5" -> Disability.Group.INTELLECTUAL
            "6" -> Disability.Group.LEARNING
            "7" -> Disability.Group.AUTISM
            else -> null
        }
    }
}

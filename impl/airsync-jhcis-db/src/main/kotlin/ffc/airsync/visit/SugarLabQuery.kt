package ffc.airsync.visit

import ffc.airsync.Dao
import ffc.airsync.MySqlJdbi
import ffc.airsync.getLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

internal class SugarLabQuery(private val jdbiDao: Dao = MySqlJdbi(null)) {
    private val logger = getLogger(this)
    private val query = """
SELECT
	visitlabsugarblood.sugarnumdigit
FROM
    visitlabsugarblood
WHERE
	visitlabsugarblood.pcucode = :pcucode
AND visitlabsugarblood.visitno = :visitnumber
    """

    fun get(pcuCode: String, visitNumber: Int): Double? {
        var i = 0
        while (true) {
            try {
                return getSugar(pcuCode, visitNumber)
            } catch (ex: Exception) {
                if (i > 5) throw ex
                i++
                logger.warn(ex) { "Database query error Loop $i message: ${ex.message}" }
                runBlocking { delay(10000) }
            }
        }
    }

    private fun getSugar(pcuCode: String, visitNumber: Int): Double? {
        return jdbiDao.instant.withHandle<List<Double>, Exception> { handle ->
            handle.createQuery(query)
                .bind("pcucode", pcuCode)
                .bind("visitnumber", visitNumber)
                .map { rs, _ ->
                    rs.getDouble("sugarnumdigit")
                }.list()
        }.firstOrNull()
    }
}

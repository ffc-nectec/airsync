package ffc.airsync.disability

import ffc.airsync.Dao
import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.airsync.getLogger
import ffc.entity.healthcare.Disability
import ffc.entity.healthcare.Disease

class DisabilityJdbi(
    private val jdbiDao: Dao = MySqlJdbi(null)
) : DisabilityDao {
    private val logger = getLogger(this)

    init {
        logger.debug { "S0" }
    }

    override fun get(lookupDisease: (icd10: String) -> Disease?): List<Triple<String, String, Disability>> {
        logger.debug { "S1" }
        val fromDb = jdbiDao.extension<QueryDisability, List<DisabilityData>> { get() }
            .mapNotNull {
                if (it.pcuCode.isNotBlank() && it.pid.isNotBlank())
                    Triple(it.pcuCode, it.pid, it.dis)
                else
                    null
            }
        logger.debug { "S2" }
        return fromDb.mapNotNull {
            val dis = it.third ?: return@mapNotNull null
            Triple(
                it.first, it.second,
                Disability(
                    dis.group,
                    dis.detectDate,
                    dis.startDate,
                    dis.cause,
                    dis.disease?.let { lookupDisease(it.name) },
                    dis.severity
                )
            )
        }
    }
}

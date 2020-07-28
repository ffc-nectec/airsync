package ffc.airsync.disability

import ffc.airsync.Dao
import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.healthcare.Disability
import ffc.entity.healthcare.Disease

class DisabilityJdbi(
    private val jdbiDao: Dao = MySqlJdbi(null)
) : DisabilityDao {
    override fun get(lookupDisease: (icd10: String) -> Disease?): List<Triple<String, String, Disability>> {
        val fromDb = jdbiDao.extension<QueryDisability, List<Triple<String, String, Disability>?>> { get() }
            .mapNotNull {
                it
            }

        return fromDb.map {
            val dis = it.third
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

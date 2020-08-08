package ffc.airsync.disability

import ffc.entity.healthcare.Disability
import ffc.entity.healthcare.Icd10

interface DisabilityDao {
    fun get(
        pcuCode: String,
        pid: String,
        lookupDisease: (icd10: String) -> Icd10?
    ): List<Disability>
}

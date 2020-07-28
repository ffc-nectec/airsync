package ffc.airsync.disability

import ffc.entity.healthcare.Disability
import ffc.entity.healthcare.Disease

interface DisabilityDao {
    /**
     * @return pcucode, pid , Disability
     */
    fun get(lookupDisease: (icd10: String) -> Disease?): List<Triple<String, String, Disability>>
}

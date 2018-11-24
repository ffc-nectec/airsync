package ffc.airsync.api.icd10

import ffc.entity.healthcare.SpecialPP

interface SpecialPpApi {
    fun lookup(id: String): SpecialPP.PPType
}

package ffc.airsync.api.icd10

import ffc.airsync.api.specialPP.RetofitSpecialPpApi
import ffc.entity.healthcare.SpecialPP

interface SpecialPpApi {
    fun lookup(id: String): SpecialPP.PPType
}

val specialPpApi: SpecialPpApi by lazy { RetofitSpecialPpApi() }

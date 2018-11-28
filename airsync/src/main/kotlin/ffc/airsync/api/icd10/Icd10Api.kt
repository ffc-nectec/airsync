package ffc.airsync.api.icd10

import ffc.entity.healthcare.Icd10

interface Icd10Api {
    fun lookup(icd10: String): Icd10
}

val icd10Api: Icd10Api by lazy { RetofitIcd10Api() }

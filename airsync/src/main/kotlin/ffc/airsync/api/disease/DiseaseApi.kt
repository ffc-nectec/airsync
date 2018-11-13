package ffc.airsync.api.disease

import ffc.entity.healthcare.Disease

interface DiseaseApi {
    fun lookup(icd10: String): List<Disease>
}

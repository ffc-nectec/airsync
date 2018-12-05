package ffc.airsync.api.icd10

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi
import ffc.entity.healthcare.Icd10

class RetofitIcd10Api : RetofitApi<Icd10Url>(Icd10Url::class.java, 10240), Icd10Api {
    override fun lookup(icd10: String): Icd10 {

        return callApi {
            restService.lookupIcd10(
                authkey = tokenBarer,
                id = icd10
            ).execute().body() ?: Icd10(icd10 = icd10, id = icd10, name = "")
        }
    }
}

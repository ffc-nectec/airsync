package ffc.airsync.api.icd10

import ffc.airsync.printDebug
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi
import ffc.entity.healthcare.Icd10

class RetofitIcd10Api : RetofitApi<Icd10Url>(Icd10Url::class.java, 10240), Icd10Api {
    override fun lookup(icd10: String): Icd10 {

        return callApi {
            val response = restService.lookupIcd10(
                authkey = tokenBarer,
                id = icd10
            ).execute()
            if (response.code() != 200) {
                val errorBody = response.errorBody()?.byteStream()?.reader()?.readLines()
                printDebug("Error LookupICD10=$icd10 error=${response.code()} body=$errorBody")
            }
            response.body() ?: Icd10(icd10 = icd10, id = icd10, name = "")
        }
    }
}

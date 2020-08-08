package ffc.airsync.api.icd10

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi
import ffc.airsync.utils.getLogger
import ffc.entity.healthcare.Icd10

class Icd10ServiceApi : RetofitApi<Icd10Service>(Icd10Service::class.java, 10240), Icd10Api {
    private val logger by lazy { getLogger(this) }
    override fun lookup(icd10: String): Icd10 {

        val icd10Upper = icd10.toUpperCase()
        return callApi {
            val response = restService.lookupIcd10(
                authkey = tokenBarer,
                id = icd10Upper
            ).execute()
            if (response.code() != 200) {
                val errorBody = response.errorBody()?.byteStream()?.reader()?.readLines()
                val message = "Error LookupICD10=$icd10Upper error=${response.code()} body=$errorBody"
                logger.warn(Exception(message)) { message }
            }
            response.body() ?: Icd10(icd10 = icd10Upper, id = icd10Upper, name = "")
        }
    }
}

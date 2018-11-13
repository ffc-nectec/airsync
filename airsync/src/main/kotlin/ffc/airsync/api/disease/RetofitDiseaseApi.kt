package ffc.airsync.api.disease

import ffc.airsync.retrofit.RetofitApi
import ffc.entity.healthcare.Disease

class RetofitDiseaseApi : RetofitApi(), DiseaseApi {
    override fun lookup(icd10: String): List<Disease> {
        while (true) {

            try {
                val respond = restService.lookupDisease(
                    authkey = tokenBarer,
                    query = icd10
                ).execute()

                return (respond.body() ?: arrayListOf())
            } catch (ex: java.net.SocketTimeoutException) {
                ex.printStackTrace()
            }
        }
    }
}

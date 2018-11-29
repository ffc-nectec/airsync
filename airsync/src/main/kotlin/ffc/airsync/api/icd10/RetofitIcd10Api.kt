package ffc.airsync.api.icd10

import ffc.airsync.retrofit.RetofitApi
import ffc.entity.healthcare.Icd10

class RetofitIcd10Api : RetofitApi<Icd10Url>(Icd10Url::class.java), Icd10Api {
    override fun lookup(icd10: String): Icd10 {
        while (true) {
            try {
                val respond = restService.lookupIcd10(
                    authkey = tokenBarer,
                    id = icd10
                ).execute()

                return (respond.body() ?: Icd10(icd10 = icd10, id = icd10, name = ""))
            } catch (ex: java.net.SocketTimeoutException) {
                ex.printStackTrace()
            }
        }
    }
}

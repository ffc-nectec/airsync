package ffc.airsync.api.specialPP

import ffc.airsync.api.icd10.SpecialPpApi
import ffc.airsync.retrofit.RetofitApi
import ffc.entity.healthcare.SpecialPP

class RetofitSpecialPpApi : RetofitApi<SpecialPpUrl>(SpecialPpUrl::class.java), SpecialPpApi {
    override fun lookup(id: String): SpecialPP.PPType {
        while (true) {

            try {
                val respond = restService.lookupSpecialPP(
                    authkey = tokenBarer,
                    id = id
                ).execute()

                return (respond.body() ?: SpecialPP.PPType(id, ""))
            } catch (ex: java.net.SocketTimeoutException) {
                ex.printStackTrace()
            } catch (ex: java.net.UnknownHostException) {
                ex.printStackTrace()
            }
        }
    }
}

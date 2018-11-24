package ffc.airsync.api.icd10

import ffc.airsync.retrofit.RetofitApi
import ffc.entity.healthcare.SpecialPP

class RetofitSpecialPpApi : RetofitApi(), SpecialPpApi {
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

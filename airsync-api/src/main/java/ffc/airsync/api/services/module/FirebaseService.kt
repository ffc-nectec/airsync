package ffc.airsync.api.services.module

import ffc.model.FirebaseToken
import ffc.model.printDebug
import java.util.*

object FirebaseService {
    fun updateToken(token: String, orgId: String, firebaseToken: FirebaseToken) {

        try {
            val mobile = getOrgByMobileToken(token = UUID.fromString(token), orgId = orgId)
            printDebug("Update firebase token mobile $firebaseToken")
            mobile.data.firebaseToken = firebaseToken.firebasetoken
        } catch (ex: Exception) {
            val org = getOrgByOrgToken(token, orgId)
            printDebug("Update firebase token organization $firebaseToken")
            org.firebaseToken = firebaseToken.firebasetoken

        }

    }
}

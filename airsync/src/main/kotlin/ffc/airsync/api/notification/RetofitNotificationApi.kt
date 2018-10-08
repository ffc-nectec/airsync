package ffc.airsync.api.notification

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.printDebug
import ffc.entity.Organization
import ffc.entity.Token
import retrofit2.dsl.enqueue

class RetofitNotificationApi(org: Organization, serviceUrl: String, token: Token) : RetofitApi(org, serviceUrl, token),
    NotificationApi {
    override fun putFirebaseToken(firebaseToken: HashMap<String, String>) {
        restService.createFirebaseToken(
            orgId = organization.id,
            authkey = tokenBarer,
            firebaseToken = firebaseToken
        ).enqueue {
            onSuccess { printDebug("Success bind firebase to cloud") }
        }
    }
}

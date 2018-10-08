package ffc.airsync.api.notification

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.printDebug
import retrofit2.dsl.enqueue

class RetofitNotificationApi : RetofitApi(), NotificationApi {
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

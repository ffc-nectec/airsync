package ffc.airsync.api.notification

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi

class RetofitNotificationApi : RetofitApi<NotificationUrl>(NotificationUrl::class.java), NotificationApi {
    override fun registerChannel(firebaseToken: HashMap<String, String>) {
        callApi {
            restService.createFirebaseToken(
                orgId = organization.id,
                authkey = tokenBarer,
                firebaseToken = firebaseToken
            ).execute()
        }
    }
}

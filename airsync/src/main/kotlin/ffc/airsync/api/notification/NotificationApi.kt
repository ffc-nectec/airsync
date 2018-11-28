package ffc.airsync.api.notification

interface NotificationApi {
    fun registerChannel(firebaseToken: HashMap<String, String>)
}

val notificationApi: NotificationApi by lazy { RetofitNotificationApi() }

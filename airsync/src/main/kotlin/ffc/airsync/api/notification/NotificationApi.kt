package ffc.airsync.api.notification

interface NotificationApi {
    fun putFirebaseToken(firebaseToken: HashMap<String, String>)
}

package ffc.airsync.notification

interface Notification {
    fun onTokenChange(callback: (HashMap<String, String>) -> Unit)

    fun onReceiveDataUpdate(callback: (type: String, id: String) -> Unit)
}

package ffc.airsync.notification

interface Notification {

    fun onTokenChange(callback: (String) -> Unit)

    fun onReceiveDataUpdate(callback: (type: String, id: String) -> Unit)
}

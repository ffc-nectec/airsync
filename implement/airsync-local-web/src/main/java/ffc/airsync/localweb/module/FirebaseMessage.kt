package ffc.airsync.client.webservice.module

import ffc.airsync.notification.Notification
import ffc.entity.Messaging
import ffc.entity.firebase.FirebaseToken

class FirebaseMessage : Notification {

    private var identifierChange: ((String) -> Unit)? = null
    private var onDataChange: ((type: String, id: String) -> Unit)? = null

    override fun onTokenChange(callback: (String) -> Unit) {
        identifierChange = callback
    }

    override fun onReceiveDataUpdate(callback: (type: String, id: String) -> Unit) {
        onDataChange = callback
    }

    companion object {
        private val instant = FirebaseMessage()

        fun getInstance() = instant
    }

    fun updateToken(firebaseToken: FirebaseToken) {
        identifierChange?.invoke(firebaseToken.firebasetoken)
    }

    fun updateHouse(data: Messaging) {
        onDataChange?.invoke(data.type, data.id)
    }
}

package ffc.airsync.client.webservice.module

import ffc.entity.Messaging
import ffc.entity.firebase.FirebaseToken

class FirebaseMessage {

    private constructor()

    interface OnUpdateListener {
        fun onUpdate(token: FirebaseToken)
    }

    interface OnUpdateHouseListener {
        fun onUpdate(_id: String)
    }

    var onUpdateListener: OnUpdateListener? = null
    var onUpdateHouseListener: OnUpdateHouseListener? = null


    companion object {
        val instant = FirebaseMessage()
    }

    fun updateToken(firebaseToken: FirebaseToken) {
        onUpdateListener?.onUpdate(firebaseToken)
    }

    fun updateHouse(data: Messaging) {
        onUpdateHouseListener?.onUpdate(data._id)
    }
}

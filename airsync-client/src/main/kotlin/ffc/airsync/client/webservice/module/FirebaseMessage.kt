package ffc.airsync.client.webservice.module

import ffc.model.FirebaseToken

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

    fun updateHouse(data: ffc.model.FirebaseMessage.Data) {
        onUpdateHouseListener?.onUpdate(data._id)
    }
}

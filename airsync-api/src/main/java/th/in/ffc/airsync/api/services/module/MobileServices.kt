package th.`in`.ffc.airsync.api.services.module

import th.`in`.ffc.module.struct.obj.MessageSync
import th.`in`.ffc.module.struct.obj.Pcu
import th.`in`.ffc.module.struct.obj.mobiletoken.MobileUserAuth
import java.util.*


interface MobileServices {
    interface OnReceiveListener{
        fun onReceive(message :String)
    }
    var onReceiveListener : OnReceiveListener?
        get() = onReceiveListener
        set(value) {}

    fun getAll() : List<Pcu>
    fun getMyPcu(ipAddress : String): List<Pcu>
    fun registerMobile(mobileUserAuth: MobileUserAuth) : MessageSync
    fun sendAndRecive(messageSync: MessageSync, onReceiveListener: OnReceiveListener, pcu: Pcu = Pcu(UUID.randomUUID()))

    //fun sendPcu(messageSync: MessageSync,onReceiveListener : OnReceiveListener,pcu :PcuResource = PcuResource("","", UUID.randomUUID(),"",""))

    /*fun recivePcu(message: String){
        onReceiveListener?.onReceive(message)
    }*/

}

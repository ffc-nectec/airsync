package th.`in`.ffc.airsync.api.websocket.module

import com.google.gson.Gson
import org.eclipse.jetty.websocket.api.Session
import java.util.*


interface PcuService  {
    interface onReciveMessage {
        fun setOnReceiveMessage(message: String)
    }
    companion object {
        val connectionMap = HashMap<String, Session>()
        val gson = Gson()
        val mobileHashMap = HashMap<UUID, PcuService.onReciveMessage>()

    }

    fun getSession():String
    fun receiveTextData(message :String)
    fun getSessionObject() :Session

}

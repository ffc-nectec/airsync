package th.`in`.ffc.airsync.api.websocket.module

import com.google.gson.Gson
import org.eclipse.jetty.websocket.api.WebSocketAdapter
import java.util.*

interface PcuService {
    interface onReciveMessage {
        fun setOnReceiveMessage(message: String)
    }

    companion object {
        val gson = Gson()
        val connectionMap = HashMap<String, WebSocketAdapter>()
        val mobileHashMap = HashMap<UUID, onReciveMessage>()
    }

}

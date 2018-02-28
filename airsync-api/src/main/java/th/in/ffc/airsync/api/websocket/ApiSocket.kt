package th.`in`.ffc.airsync.api.websocket


import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter
import th.`in`.ffc.airsync.api.websocket.module.PcuService
import th.`in`.ffc.airsync.api.websocket.module.PcuWebSocketService

class ApiSocket : WebSocketAdapter() {

    var pcuService: PcuService? = null

    override fun onWebSocketConnect(sess: Session?) {
        super.onWebSocketConnect(sess)
        if (sess != null) {
            pcuService = PcuWebSocketService(sess)
        }
    }

    override fun onWebSocketText(message: String?) {
        super.onWebSocketText(message)

        if (message != null) {
            pcuService?.receiveTextData(message)
        }

    }

    override fun onWebSocketClose(statusCode: Int, reason: String?) {
        super.onWebSocketClose(statusCode, reason)
        println("Socket Closed: [" + statusCode + "] " + reason)
    }

    override fun onWebSocketError(cause: Throwable?) {
        super.onWebSocketError(cause)
        cause!!.printStackTrace(System.err)
    }

}

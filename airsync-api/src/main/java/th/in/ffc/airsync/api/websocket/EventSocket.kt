package th.`in`.ffc.airsync.api.websocket


import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter


class EventSocket : WebSocketAdapter() {
    override fun onWebSocketConnect(sess: Session?) {
        super.onWebSocketConnect(sess)
        System.out.println("Socket Connected: " + sess)
    }

    override fun onWebSocketText(message: String?) {
        super.onWebSocketText(message)
        System.out.println("Received TEXT message: " + message)
    }

    override fun onWebSocketClose(statusCode: Int, reason: String?) {
        super.onWebSocketClose(statusCode, reason)
        System.out.println("Socket Closed: [" + statusCode + "] " + reason)
    }

    override fun onWebSocketError(cause: Throwable?) {
        super.onWebSocketError(cause)
        cause!!.printStackTrace(System.err)
    }
}

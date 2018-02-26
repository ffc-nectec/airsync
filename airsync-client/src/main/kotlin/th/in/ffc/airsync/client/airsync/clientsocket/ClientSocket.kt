package th.`in`.ffc.airsync.client.airsync.clientsocket

import org.apache.commons.codec.digest.DigestUtils
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter

class ClientSocket : WebSocketAdapter() {
    var session: String = ""
    var count = 0

    override fun onWebSocketConnect(sess: Session?) {
        super.onWebSocketConnect(sess)
        System.out.println("Socket Connected: " + sess)
        this.session = DigestUtils.sha1Hex(sess.toString())
        System.out.println("Session= " + this.session)
    }

    override fun onWebSocketText(message: String?) {
        super.onWebSocketText(message)
        System.out.println("Session " + session)
        System.out.println("Count:" + (count++) + "\tReceived TEXT message: " + message)
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

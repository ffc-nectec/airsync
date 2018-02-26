package th.`in`.ffc.airsync.client.airsync.clientsocket

import com.google.gson.Gson
import org.apache.commons.codec.digest.DigestUtils
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter
import th.`in`.ffc.module.struct.MessageSync

class ClientSocket : WebSocketAdapter() {
    companion object {
        val gson = Gson()
    }

    var session: String = ""
    var count = 0
    var clientStatus = 0

    override fun onWebSocketConnect(sess: Session?) {
        super.onWebSocketConnect(sess)
        println("Socket Connected: " + sess)
        this.session = DigestUtils.sha1Hex(sess.toString())
        println("Session= " + this.session)
    }

    override fun onWebSocketText(message: String?) {
        super.onWebSocketText(message)
        println("onWebSocketText")
        //println("Session " + session)
        println("Count:" + (count++) + "\tReceived TEXT message: " + message)

        if (!message.equals("H")) {
            val messageSync = gson.fromJson(message, MessageSync::class.java)
            println("Status " + messageSync.status +" Action = "+ messageSync.action+ " Message = " + messageSync.message)


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

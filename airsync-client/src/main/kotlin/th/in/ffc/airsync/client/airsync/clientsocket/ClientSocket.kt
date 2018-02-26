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
    var clientStatus =0

    override fun onWebSocketConnect(sess: Session?) {
        super.onWebSocketConnect(sess)
        System.out.println("Socket Connected: " + sess)
        this.session = DigestUtils.sha1Hex(sess.toString())
        System.out.println("Session= " + this.session)
    }

    override fun onWebSocketText(message: String?) {
        super.onWebSocketText(message)
        println("onWebSocketText")
        //System.out.println("Session " + session)
        System.out.println("Count:" + (count++) + "\tReceived TEXT message: " + message)

        if(clientStatus==0){// Init
            val messageSync = gson.fromJson(message,MessageSync::class.java)
            println("Status "+messageSync.status+" Message = "+messageSync.message)
            clientStatus = 1
        }else if(clientStatus==1){

        }

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

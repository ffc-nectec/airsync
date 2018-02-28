package th.`in`.ffc.airsync.client.airsync.client.module

import org.apache.commons.codec.digest.DigestUtils
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter
import th.`in`.ffc.module.struct.obj.mobiletoken.MobileUserAuth
import th.`in`.ffc.module.struct.obj.MessageSync
import java.util.*

class PcuSocketEvent : WebSocketAdapter() {


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
        println("Count:" + (count++) + "\tReceived TEXT message: " + message)

        if (!message.equals("H")) {
            val messageSync = GsonConvert.gson.fromJson(message, MessageSync::class.java)
            println("Status " + messageSync.status +" Action = "+ messageSync.action+ " Message = " + messageSync.message)

            if(messageSync.action==1){// Action 1 Check username
                println("Check Auth")
                val mobileSync= GsonConvert.gson.fromJson(messageSync.message, MobileUserAuth::class.java)
                if(mobileSync.username.equals("adminffcair") && mobileSync.password.equals("ffc@irffc@ir")){

                    messageSync.status=200
                    messageSync.to =UUID.fromString(mobileSync.mobileUuid.toString())
                    println("Auth pass")
                }else{
                    messageSync.status=-1
                    println("Not pass")
                }
                this.getSession().remote.sendString(GsonConvert.gson.toJson(messageSync))
            }else if (messageSync.action==10){//Replay Message
                println("Replay Message = "+messageSync.message)
                switSendTo(messageSync)
                this.getSession().remote.sendString(GsonConvert.gson.toJson(messageSync))
            }

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
    private fun switSendTo(messageSync: MessageSync){
        val uuidBackup = messageSync.to
        messageSync.to=messageSync.from
        messageSync.from=uuidBackup
    }
}

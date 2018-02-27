package th.`in`.ffc.airsync.api.websocket


import com.google.gson.Gson
import org.apache.commons.codec.digest.DigestUtils
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter
import th.`in`.ffc.airsync.api.dao.GsonConvert
import th.`in`.ffc.airsync.api.services.Connecter
import th.`in`.ffc.module.struct.obj.MessageSync
import th.`in`.ffc.module.struct.obj.Pcu
import java.util.*
import kotlin.collections.HashMap


class ApiSocket : WebSocketAdapter() {
    interface onReciveMessage {
        fun setOnReceiveMessage(message: String)
    }
    companion object {
        val gson = Gson()
        val connectionMap = HashMap<String,WebSocketAdapter>()
        val mobileHashMap = HashMap<UUID, onReciveMessage>()
    }



    var session : String = ""
    var count = 0
    var stage = 0  //stage 0:init   1:run
    var pcu : Pcu = Pcu("", "", UUID.randomUUID(), "", "")


    override fun onWebSocketConnect(sess: Session?) {
        super.onWebSocketConnect(sess)
        this.session= DigestUtils.sha1Hex(sess.toString())
        connectionMap.put(session,this)
        println("onWebSocketConnect "+this.session)

    }

    override fun onWebSocketText(message: String?) {
        super.onWebSocketText(message)
        println("onWebSocketText " + session)
        println("Stage = "+stage+" Count:"+(count++)+"\tMessage: " + message)

        if(stage==0){//Register PCU
            val pcu = gson.fromJson(message, Pcu::class.java)

            this.pcu=Pcu(pcu.Code,pcu.Name, UUID.fromString(pcu.uuid.toString()),session,this.remote.inetSocketAddress.address.hostAddress)


            Connecter.register.registerPcu(this.pcu)
            stage=1
            val messageOk= MessageSync(UUID.randomUUID(),UUID.fromString(pcu.uuid.toString()),200, message = "H")
            this.getSession().remote.sendString(gson.toJson(messageOk))

        }else if (stage ==1){
            println(message)
            if(message.equals("H")){//Health connection.
                this.getSession().remote.sendString("H")
            }else{//Proxy
                val messageSync = GsonConvert.gson.fromJson(message, MessageSync::class.java)
                println("Status " + messageSync.status +" Action = "+ messageSync.action+ " Message = " + messageSync.message)
                if(messageSync.action==1){//Send Auth to mobile.
                    mobileHashMap.get(messageSync.to)?.setOnReceiveMessage(GsonConvert.gson.toJson(messageSync))


                }




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


}

package th.`in`.ffc.airsync.client.airsync.client.module

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.client.WebSocketClient
import th.`in`.ffc.airsync.client.airsync.client.module.PcuSocketEvent
import java.net.URI
import java.util.concurrent.Future

class PcuSocketEventManage(uri: URI) {
    //http://188.166.249.72

    //val uri = URI.create("ws://188.166.249.72:80/airsync")
    val client = WebSocketClient()
    val socket = PcuSocketEvent()
    var session: Session? = null

    fun sendText(message: String) {
        if (session != null) {
            session!!.getRemote().sendString(message)
        }else{
            throw NoSuchFieldException("Session Null")
        }
    }
    fun close(){
        // Close session
        this.session?.close()
        client.stop()
    }

    init {
        try {
            try {
                client.start()
                // Attempt Connect
                val fut: Future<Session> = client.connect(socket, uri)
                // Wait for Connect
                this.session = fut.get()

            } finally {

            }
        } catch (t: Throwable) {
            t.printStackTrace(System.err)
        }
    }

}

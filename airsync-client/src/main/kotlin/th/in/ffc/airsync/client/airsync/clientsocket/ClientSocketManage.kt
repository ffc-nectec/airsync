package th.`in`.ffc.airsync.client.airsync.clientsocket

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.client.WebSocketClient
import java.net.URI
import java.util.concurrent.Future

class ClientSocketManage {
    val uri = URI.create("ws://127.0.0.1:8080/airsync");
    val client = WebSocketClient()
    val socket = ClientSocket()
    var session: Session? = null

    constructor() {
        try {
            try {
                client.start()
                // Attempt Connect
                val fut: Future<Session> = client.connect(socket, uri)
                // Wait for Connect
                this.session = fut.get()
                // Send a message

                // Close session
                //this.session.close()

            } finally {

                //client.stop()
            }
        } catch (t: Throwable) {
            t.printStackTrace(System.err)
        }
    }

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

}

package th.`in`.ffc.airsync.apiwebsock

import java.net.URI;
import java.util.concurrent.Future;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Test
import th.`in`.ffc.airsync.api.websocket.ApiSocket

class SocketTest {

    @Test
    fun test1() {

        val uri = URI.create("ws://127.0.0.1:8080/airsync");

        val client = WebSocketClient()
        val socket: ApiSocket
        try {
            try {
                client.start()
                // The socket that receives events
                socket = ApiSocket()
                // Attempt Connect
                val fut: Future<Session> = client.connect(socket, uri)
                // Wait for Connect
                val session: Session = fut.get()
                // Send a message
                while (true) {
                    session.getRemote().sendString("Hello")
                    Thread.sleep(5000)
                }
                // Close session
                session.close()

            } finally {

                client.stop()
            }
        } catch (t: Throwable) {
            t.printStackTrace(System.err);
        }
    }
}

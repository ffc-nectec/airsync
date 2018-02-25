package th.`in`.ffc.airsync.apiwebsock

import java.net.URI;
import java.util.concurrent.Future;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Test

class SocketTest {

    @Test
    fun test1(){

        val  uri = URI.create("ws://127.0.0.1:8080/v0/events");

        val client = WebSocketClient()
        try
        {
            try
            {
                client.start();
                // The socket that receives events
                val socket = EventSocket()
                // Attempt Connect
                val fut : Future<Session> = client.connect(socket,uri);
                // Wait for Connect
                val session : Session = fut.get();
                // Send a message
                session.getRemote().sendString("Hello");
                // Close session
                session.close();
            }
            finally
            {
                client.stop();
            }
        }
        catch (t :Throwable)
        {
            t.printStackTrace(System.err);
        }
    }
}

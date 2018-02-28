package th.`in`.ffc.airsync.client.airsync

import com.google.gson.Gson
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.client.WebSocketClient
import org.junit.Test
import th.`in`.ffc.airsync.client.airsync.client.module.PcuSocketEvent
import th.`in`.ffc.airsync.client.airsync.client.module.PcuSocketEventManage
import th.`in`.ffc.module.struct.obj.Pcu
import java.net.URI
import java.util.*
import java.util.concurrent.Future

class Test2 {
    val gson: Gson = Gson()
    @Test
    fun test1() {

        //val testobj: Pcu = Pcu("32432", "Nectec01", UUID.randomUUID())


        //System.out.println(
        //  gson.toJson(testobj))
        System.out.println(UUID.fromString("00000000-0000-0000-0000-000000000000"))
    }

    @Test
    fun test2(){
        val outstr: MutableList<String> = mutableListOf("sdf", "sdfasdfdsaf", "99999")
        //val testobj: QueryAction = QueryAction(outstr);

        //System.out.println(gson.toJson(testobj));
    }
    @Test
    fun testsocket() {

        val uri = URI.create("ws://127.0.0.1:8080/airsync");

        val client = WebSocketClient()
        val socket: PcuSocketEvent
        try {
            try {
                client.start()
                // The socket that receives events
                socket = PcuSocketEvent()
                // Attempt Connecter
                val fut: Future<Session> = client.connect(socket, uri)
                // Wait for Connecter
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


    @Test
    fun testAirSyncSocketAndRegister(){
        /*var client = PcuSocketEventManage()
        var pcu = Pcu("112233", "Nectec1999", UUID.randomUUID(), "sadsdafdsaf", "202.99.11.22 ")
        client.sendText(Gson().toJson(pcu))
        Thread.sleep(5000)
        client.sendText(Gson().toJson(pcu))
        client.sendText(Gson().toJson(pcu))
        client.close()*/

    }


}

/*
 * Copyright (c) 2561 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.airsync.client

import com.google.gson.Gson
import ffc.airsync.client.client.module.PcuSocketEvent
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.client.WebSocketClient
import org.junit.Test
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

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

package ffc.airsync.client.client.module

import ffc.model.Organization
import ffc.model.TokenMessage
import ffc.model.printDebug
import ffc.model.toJson
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.client.WebSocketClient
import java.net.URI
import java.util.concurrent.Future

class PcuSocketAuthByToken(override var eventCallBack: PcuSocket.OnEventCallbackMessageListener, organization :Organization) : PcuSocket {
    val client = WebSocketClient()
    var socket :BaseNetworkSocket?=null
    var sessionObj: Session? = null
    var stage = 0
    val organization :Organization


    private var healthConnectionWorking = true
    private val healthConnectionThread: Thread

    init {
        this.organization=organization
        healthConnectionThread = Thread(Runnable {
            printDebug("Thread health connection start")
            var state = 0
            while (true) {
                printDebug("Thread health connection stage = " + state)
                if (healthConnectionWorking) {
                    if (state == 0) {

                        sendText(TokenMessage(organization.token!!).toJson())
                        state = 1
                    }
                    sendText("H")
                }
                Thread.sleep(5000)
            }
        })
    }


    override fun connect(uri: URI) {
        close()
        try {
            try {
                socket = BaseNetworkSocket()
                socket!!.onWebSocketText = object :BaseNetworkSocket.OnWebSocketText{
                    override fun onWebSocketText(message: String?, count: Long) {
                        if (message != null)
                            receiveMessage(message,count)
                    }
                }
                client.start()
                // Attempt Connect
                val fut: Future<Session> = client.connect(socket, uri)
                printDebug("Connection ownAction Central")
                // Wait for Connect
                sessionObj = fut.get()

            } finally {

            }
        } catch (t: Throwable) {
            t.printStackTrace(System.err)
        }
        printDebug("Call thread health connection")
        healthConnectionThread.start()

    }

    override fun sendText(message: String) {
        printDebug("sendText ")
        if (sessionObj != null) {
            printDebug("data = " + message)
            sessionObj!!.getRemote().sendString(message)
        } else {
            throw NoSuchFieldException("Session Null")
        }
    }

    override fun receiveMessage(message: String,count :Long) {

        printDebug("Count:" + (count) + "\tReceived TEXT data: " + message)

        if (!message.equals("H")) {



            /*
            if (stage == 0) {//handcheck


                val centraltoken: TokenMessage = message!!.fromJson()
               printDebug("Clent handcheck central recive token = " + centraltoken)
                if (centraltoken.token.equals(organization.centralToken)) {
                   printDebug("Auth password handcheck")
                    stage = 1
                } else {
                    throw SecurityException("Cannot handcheck")
                }
            } else {//Message Receive
               printDebug("Message Receiver Stage = " + stage + "Message = " + message)
                    //Call get data Thread sync
                    eventCallBack.EventCallBackMessage(message)

            }
            */


        }
    }



    override fun close() {
        this.sessionObj?.close()
        client.stop()
    }


    override fun join() {
        healthConnectionThread.join()
    }
}

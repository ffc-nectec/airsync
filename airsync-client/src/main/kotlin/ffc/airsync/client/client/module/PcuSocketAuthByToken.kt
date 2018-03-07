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

import ffc.airsync.client.Main
import ffc.model.TokenMessage
import ffc.model.fromJson
import ffc.model.toJson
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.client.WebSocketClient
import java.net.URI
import java.util.concurrent.Future

class PcuSocketAuthByToken(val uri: URI) : PcuSocket {
    val client = WebSocketClient()
    var socket :BaseNetworkSocket?=null
    var sessionObj: Session? = null
    var stage = 0


    private var healthConnectionWorking = true
    private val healthConnectionThread: Thread

    init {
        healthConnectionThread = Thread(Runnable {
            println("Thread health connection start")
            var state = 0
            while (true) {
                println("Thread health connection stage = "+state)
                if (healthConnectionWorking) {
                    if (state == 0) {

                        sendText(TokenMessage(Main.pcuDataTest.pcuToken!!).toJson())
                        state = 1
                    }
                    sendText("H")
                }
                Thread.sleep(5000)
            }
        })
    }


    override fun connect() {
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
                println("Connection to Central")
                // Wait for Connect
                sessionObj = fut.get()

            } finally {

            }
        } catch (t: Throwable) {
            t.printStackTrace(System.err)
        }
        println("Call thread health connection")
        healthConnectionThread.start()

    }

    override fun sendText(message: String) {
        println("sendText ")
        if (sessionObj != null) {
            println("message = "+message)
            sessionObj!!.getRemote().sendString(message)
        } else {
            throw NoSuchFieldException("Session Null")
        }
    }

    override fun receiveMessage(message: String,count :Long) {
        //println("Count:" + (count++) + "\tReceived TEXT message: " + message)

        if (!message.equals("H")) {
            if (stage == 0) {//handcheck

                val centraltoken: TokenMessage = message!!.fromJson()
                println("Clent handcheck central recive token = " + centraltoken)
                if (centraltoken.token.equals(Main.pcuDataTest.centralToken)) {
                    println("Auth pass handcheck")
                    stage = 1
                } else {
                    throw SecurityException("Cannot handcheck")
                }
            } else {//Message Receive
                println("Message Receiver Stage = " + stage + "Message = " + message)
                if (message.equals("X")) {
                    //Call get message Thread sync
                    eventCallBack.EventCallBackMessage(message)
                }
            }
        }
    }

    override var eventCallBack: PcuSocket.OnEventCallbackMessageListener
        get() = this.eventCallBack
        set(value) {
            this.eventCallBack = value
        }

    override fun close() {
        this.sessionObj?.close()
        client.stop()
    }


    override fun join() {
        healthConnectionThread.join()
    }
}

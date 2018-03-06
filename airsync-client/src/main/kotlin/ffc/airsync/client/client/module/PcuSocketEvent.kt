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
import ffc.model.Message
import ffc.model.TokenMessage
import ffc.model.fromJson
import org.apache.commons.codec.digest.DigestUtils
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter

class PcuSocketEvent : WebSocketAdapter() {


    var session: String = ""
    var count = 0
    var stage = 0

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
            if (stage==0){//handcheck

                val centraltoken :TokenMessage = message!!.fromJson()
                println("Clent handcheck central recive token = "+centraltoken)
                if(centraltoken.token.equals(Main.pcuDataTest.centralToken)){
                    println("Auth pass handcheck")
                    stage = 1
                }else
                {
                    throw SecurityException("Cannot handcheck")
                }
            }else{//Message Receive
                println("Message Receiver Stage = " + stage +"Message = "+message)
                if(message.equals("X")){
                    //Call get message Thread sync
                }
            }

            /*val messageSync = GsonConvert.gson.fromJson(message, Message::class.java)
            println("Status " + messageSync.status +" Action = "+ messageSync.action+ " Message = " + messageSync.message)

            if (messageSync.action == Message.Action.REGISTER) {// Action 1 Check username
                println("Check Auth")
                val mobileSync= GsonConvert.gson.fromJson(messageSync.message, MobileUserAuth::class.java)
                if(mobileSync.username.equals("adminffcair") && mobileSync.password.equals("ffc@irffc@ir")){

                    messageSync.status=Message.Status.SUCC
                    messageSync.to =UUID.fromString(mobileSync.mobileUuid.toString())
                    println("Auth pass")
                }else{
                    messageSync.status=Message.Status.ERROR
                    println("Not pass")
                }
                this.getSession().remote.sendString(GsonConvert.gson.toJson(messageSync))
            } else if (messageSync.action == Message.Action.PING) {//Replay Message
                println("Replay Message = "+messageSync.message)
                switSendTo(messageSync)
                this.getSession().remote.sendString(GsonConvert.gson.toJson(messageSync))
            }*/

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

    private fun switSendTo(message: Message) {
        val uuidBackup = message.to
        message.to = message.from
        message.from = uuidBackup
    }
}

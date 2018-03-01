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

package ffc.airsync.api.websocket.module

import ffc.airsync.api.dao.*
import ffc.model.Message
import ffc.model.Pcu
import org.apache.commons.codec.digest.DigestUtils
import org.eclipse.jetty.websocket.api.Session
import ffc.airsync.api.websocket.module.PcuService.Companion.connectionMap
import ffc.airsync.api.websocket.module.PcuService.Companion.mobileHashMap
import java.util.*

class PcuWebSocketService(val sess: Session) : PcuService {


    private var session: String = ""
    private var count = 0
    private var stage = 0  //stage 0:init   1:run
    private var pcu: Pcu = Pcu()
    var pcuDao: PcuDao = DaoFactory().buildPcuDao()

    init {
        this.session = DigestUtils.sha1Hex(sess.toString())
        println("onWebSocketConnect " + this.session)
        connectionMap.put(this.session, sess)
        println("Test find session before add "+ connectionMap.get(this.session)!!.remote.inetSocketAddress.hostName)
        println("Test find session before add 2 "+ connectionMap.get(this.session)!!.remote.inetSocketAddress.hostName)

    }


    override fun getSession(): String {
        return session
    }

    override fun getSessionObject(): Session {
        return sess
    }



    override fun receiveTextData(message: String) {
        println("onWebSocketText " + session)
        println("Stage = " + stage + " Count:" + (count++) + "\tMessage: " + message)


        if (message.equals("H")) {//Heatbeat
            sess.remote.sendString("H")
        } else {
            //val messageObj: Message = message.fromJson()
            if (stage == 0) {//Register PCU
                //
                val pcu :Pcu =message.fromJson()
                pcu.session=this.session
                pcu.lastKnownIp=sess.remoteAddress.hostName
                this.pcu = pcu
                pcuDao.insert(pcu)
                stage = 1
                val messageConfirmOK = Message(UUID.randomUUID(), UUID.fromString(pcu.uuid.toString()), 200, message = "H")
                sess.remote.sendString(messageConfirmOK.toJson())

            } else if (stage == 1) { //Brocker
                println(message)
                val messageSync = GsonConvert.gson.fromJson(message, Message::class.java)
                println("Status " + messageSync.status + " Action = " + messageSync.action + " Message = " + messageSync.message)
                mobileHashMap.get(messageSync.to)?.setOnReceiveMessage(GsonConvert.gson.toJson(messageSync))

            }
        }
    }
}

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

package ffc.airsync.api.services.module

import ffc.airsync.api.dao.*
import ffc.airsync.api.websocket.module.PcuService
import ffc.model.Message
import ffc.model.Mobile
import ffc.model.MobileUserAuth
import ffc.model.Pcu
import java.util.*

class MobileHttpRestService : MobileServices {

    var mobileDao: MobileDao = DaoFactory().buildMobileDao()
    var pcuDao: PcuDao = DaoFactory().buildPcuDao()

    override fun getAll(): List<Pcu> {
        val pculist = ArrayList<Pcu>()
        pculist.addAll(pcuDao.find())
        return pculist
    }

    override fun getMyPcu(ipAddress: String): List<Pcu> {
        val pculist = ArrayList<Pcu>()
        pculist.add(pcuDao.findByIpAddress(ipAddress))
        return pculist
    }

    override fun registerMobile(mobileUserAuth: MobileUserAuth): Message {

        val message = Message(mobileUserAuth.mobileUuid, mobileUserAuth.pcu.uuid, Message.Status.DEFAULT, Message.Action.REGISTER, mobileUserAuth.toJson())
        var messageReturn = Message(UUID.randomUUID(), UUID.randomUUID(), Message.Status.ERROR, Message.Action.DEFAULT, "")
        println("registerMobile \n Message =" + mobileUserAuth.toJson())
        val pcu = pcuDao.findByUuid(mobileUserAuth.pcu.uuid)




        sendAndRecive(message, object : MobileServices.OnReceiveListener {
            override fun onReceive(message: String) {
                messageReturn = message.fromJson()
                if (messageReturn.status == Message.Status.SUCC) {
                    mobileDao.insert(Mobile(messageReturn.to, pcu))
                    println("Register Mobile " + messageReturn.to.toString())
                }
            }

        }, pcu)

        return messageReturn
    }

    override fun sendAndRecive(message: Message, onReceiveListener: MobileServices.OnReceiveListener, pcu: Pcu) {

        val pcu2: Pcu
        println("sendAndRecive")
        println("Pcu = "+pcu.toJson())

        if (pcu.code.equals("099912")) {
            pcu2 = mobileDao.findByUuid(message.from).pcu
        } else {
            pcu2 = pcu
        }
        println("Pcu2 = "+pcu2.toJson())
        message.to = pcu2.uuid


        val pcuNetwork = PcuService.connectionMap.get(pcu2.session)

        println("Mobile find session Pcu = ")
        //print(pcuNetwork!!.remote.inetSocketAddress.hostName)

        if (pcuNetwork != null) {
            println("pcuNetwork Not Null")
            var waitReciveData = true
            var count = 0
            PcuService.mobileHashMap.put(message.from, object : PcuService.onReciveMessage {
                override fun setOnReceiveMessage(message: String) {
                    println("messageReceive")
                    onReceiveListener.onReceive(message)
                    waitReciveData = false
                }
            })
            pcuNetwork.remote.sendString(message.toJson())
            while (waitReciveData && count < 10) {
                count++
                Thread.sleep(2000)
                println("Wait Count " + count)
            }
        }
    }
}

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

import ffc.airsync.api.dao.DaoFactory
import ffc.airsync.api.dao.MobileDao
import ffc.airsync.api.dao.PcuDao
import ffc.airsync.api.websocket.module.PcuEventService
import ffc.model.Message
import ffc.model.MobileUserAuth
import ffc.model.Pcu
import ffc.model.toJson
import java.util.*

class MobileHttpRestServiceV2 : MobileServices {

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

    override fun registerMobile(mobileUserAuth: MobileUserAuth): Message<MobileUserAuth> {

        val userStore = DaoFactory().buildUserAuthDao()
        userStore.insert(mobileUserAuth)


        sendToPcu(Message(to = mobileUserAuth.pcu.uuid,from = mobileUserAuth.mobileUuid,data = "X"))

        /*
        val data = Message(mobileUserAuth.mobileUuid, mobileUserAuth.pcu.uuid, Message.Status.DEFAULT, Message.Action.REGISTER, mobileUserAuth.toJson())
        var messageReturn = Message(UUID.randomUUID(), UUID.randomUUID(), Message.Status.ERROR, Message.Action.DEFAULT, "")
        println("registerMobile \n Message =" + mobileUserAuth.toJson())
        val pcu = pcuDao.findByUuid(mobileUserAuth.pcu.uuid)


        sendAndRecive(data, object : MobileServices.OnReceiveListener {
            override fun onReceive(data: String) {
                messageReturn = data.fromJson()
                if (messageReturn.status == Message.Status.SUCC) {
                    mobileDao.insert(Mobile(messageReturn.ownAction, pcu))
                    println("Register Mobile " + messageReturn.ownAction.toString())
                }
            }

        }, pcu)
*/
        val messageReturn: Message<MobileUserAuth> = Message(UUID.randomUUID(), UUID.randomUUID(), Message.Status.SUCC, Message.Action.DEFAULT)
        return messageReturn
    }

    fun <T> sendToPcu2(message: Message<T>){
        val pcu2: Pcu =pcuDao.findByUuid(message.to) //Not safe.
        //val pcu2 = mobileDao.findByUuid(data.from).pcu   //Safe.
        println("sendToPcu")

        println("Pcu2 = "+pcu2.toJson())
        message.to = pcu2.uuid


        //val pcuNetwork = PcuEventService.connectionMap.get(pcu2.session)
        val pcuNetwork = PcuEventService.connectionMap.get(pcu2.session)
        //println("Mobile find session Pcu = ")
        //print(pcuNetwork!!.remote.inetSocketAddress.hostName)


        if (pcuNetwork != null) {
            println("pcuNetwork Not Null")
            pcuNetwork.remote.sendString(message.toJson())
        }
    }

    override fun <T> sendToPcu(message: Message<T>) {
        val pcu2 = mobileDao.findByUuid(message.from).pcu   //Safe.
        message.to = pcu2.uuid
        sendToPcu2(message)
    }

    override fun <T> sendAndRecive(message: Message<T>, onReceiveListener: MobileServices.OnReceiveListener, pcu: Pcu) {

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


        val pcuNetwork = PcuEventService.connectionMap.get(pcu2.session)

        println("Mobile find session Pcu = ")
        //print(pcuNetwork!!.remote.inetSocketAddress.hostName)

        if (pcuNetwork != null) {
            println("pcuNetwork Not Null")
            var waitReciveData = true
            var count = 0
            PcuEventService.mobileHashMap.put(message.from, object : PcuEventService.onReciveMessage {
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

package th.`in`.ffc.airsync.api.websocket.module

import org.apache.commons.codec.digest.DigestUtils
import org.eclipse.jetty.websocket.api.Session
import th.`in`.ffc.airsync.api.dao.DaoFactory
import th.`in`.ffc.airsync.api.dao.GsonConvert
import th.`in`.ffc.airsync.api.dao.PcuDao
import th.`in`.ffc.airsync.api.websocket.module.PcuService.Companion.connectionMap
import th.`in`.ffc.airsync.api.websocket.module.PcuService.Companion.gson
import th.`in`.ffc.airsync.api.websocket.module.PcuService.Companion.mobileHashMap
import th.`in`.ffc.module.struct.obj.MessageSync
import th.`in`.ffc.module.struct.obj.Pcu
import java.util.*

class PcuWebSocketService(val sess: Session) : PcuService {


    private var session: String = ""
    private var count = 0
    private var stage = 0  //stage 0:init   1:run
    private var pcu: Pcu = Pcu()
    var pcuDao: PcuDao = DaoFactory().buildPcuDao()

    init {
        this.session = DigestUtils.sha1Hex(sess.toString())
        connectionMap.put(this.session, sess)
        println("onWebSocketConnect " + this.session)
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
            if (stage == 0) {//Register PCU
                val pcu = gson.fromJson(message, Pcu::class.java)
                this.pcu = pcu

                pcuDao.insert(this.pcu)
                stage = 1
                val messageConfirmOK = MessageSync(UUID.randomUUID(), UUID.fromString(pcu.uuid.toString()), 200, message = "H")
                sess.remote.sendString(gson.toJson(messageConfirmOK))

            } else if (stage == 1) { //Brocker
                println(message)
                val messageSync = GsonConvert.gson.fromJson(message, MessageSync::class.java)
                println("Status " + messageSync.status + " Action = " + messageSync.action + " Message = " + messageSync.message)
                mobileHashMap.get(messageSync.to)?.setOnReceiveMessage(GsonConvert.gson.toJson(messageSync))

            }
        }
    }
}

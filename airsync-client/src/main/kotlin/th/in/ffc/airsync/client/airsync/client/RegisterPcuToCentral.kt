package th.`in`.ffc.airsync.client.airsync.client

import th.`in`.ffc.airsync.client.airsync.client.module.GsonConvert
import th.`in`.ffc.module.struct.obj.Pcu

class RegisterPcuToCentral {

    fun register(pcu: Pcu){

        NetworkClient.client.sendText(GsonConvert.gson.toJson(pcu))
    }

}

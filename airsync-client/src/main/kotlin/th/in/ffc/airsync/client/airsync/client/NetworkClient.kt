package th.`in`.ffc.airsync.client.airsync.client

import th.`in`.ffc.airsync.client.airsync.client.module.PcuSocketEventManage

class NetworkClient {
    companion object {
        var client = PcuSocketEventManage(Config.uri)
    }
}

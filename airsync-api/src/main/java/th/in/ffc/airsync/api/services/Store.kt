package th.`in`.ffc.airsync.api.services

import th.`in`.ffc.airsync.api.dao.RegisterByObject
import th.`in`.ffc.airsync.api.dao.RegisterDAO

class Store {
    companion object {
        //var store : RegisterDAO = RegisterElasticSearch()
        var store: RegisterDAO = RegisterByObject()
    }
}

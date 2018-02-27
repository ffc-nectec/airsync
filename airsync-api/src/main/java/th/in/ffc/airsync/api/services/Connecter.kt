package th.`in`.ffc.airsync.api.services

import th.`in`.ffc.airsync.api.RegisterByObject
import th.`in`.ffc.airsync.api.RegisterDAO
import th.`in`.ffc.airsync.api.RegisterElasticSearch
import th.`in`.ffc.airsync.api.dao.HashMapsConnecter

class Connecter {
    companion object {
        //var connecter = HashMapsConnecter()
        //var register : RegisterDAO = RegisterElasticSearch()
        var register : RegisterDAO = RegisterByObject()
    }
}

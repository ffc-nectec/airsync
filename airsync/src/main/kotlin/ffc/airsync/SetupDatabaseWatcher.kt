package ffc.airsync

import ffc.airsync.api.house.houseApi
import ffc.airsync.db.DatabaseDao
import ffc.airsync.utils.printDebug
import ffc.entity.place.House
import ffc.entity.update

class SetupDatabaseWatcher(val dao: DatabaseDao) {

    init {
        databaseWatcher()
    }

    private fun databaseWatcher() {
        val filter = hashMapOf<String, List<String>>().apply {
            put("house", listOf("house", "`house`", "`jhcisdb`.`house`"))
            put("visit", listOf("visit", "`visit`"))
        }

        ffc.airsync.provider.databaseWatcher(
            Config.logfilepath, filter
        ) { tableName, keyWhere ->
            printDebug("Database watcher $tableName $keyWhere")
            when (tableName) {
                "house" -> {
                    if (keyWhere.size == 1) {
                        val house = dao.getHouse(keyWhere.first())
                        house.forEach {
                            try {
                                val houseSync = findHouseWithKey(it)
                                houseSync.update(it.timestamp) {
                                    road = it.road
                                    no = it.no
                                    location = it.location
                                    link!!.isSynced = true
                                }

                                houseApi.syncHouseToCloud(houseSync)
                            } catch (ignore: NullPointerException) {
                            }
                        }
                    }
                }
                "visit" -> {
                    printDebug("visit t:$tableName k:$keyWhere")
                }
            }
        }.start()
    }

    private fun findHouseWithKey(house: House): House {
        val houseFind = houses.find {
            house.link!!.keys["pcucode"] == it.link!!.keys["pcucode"] &&
                    house.link!!.keys["hcode"] == it.link!!.keys["hcode"]
        }

        return houseFind ?: throw NullPointerException("ค้นหาไม่พบบ้าน")
    }
}

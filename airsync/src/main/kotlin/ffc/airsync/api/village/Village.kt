package ffc.airsync.api.village

import ffc.airsync.Main
import ffc.airsync.utils.checkNewDataCreate
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.airsync.villageApi
import ffc.airsync.villages
import ffc.entity.Village
import ffc.entity.gson.toJson

val VILLAGELOOKUP = { jVillageId: String ->
    villages.find { it.link!!.keys["villcode"].toString() == jVillageId }
}

/**
 * ดึงข้อมูลรายการหมู่บ้านจาก jhcisdb
 */
fun List<Village>.getVillage() = Main.instant.dao.getVillage()

fun ArrayList<Village>.initSync() {
    val cacheFile = arrayListOf<Village>().apply { addAll(load()) }
    val jhcisVillage = cacheFile.getVillage()

    if (cacheFile.isEmpty()) {
        addAll(villageApi.toCloud(jhcisVillage))
        save()
    } else {
        addAll(cacheFile)
        checkNewDataCreate(jhcisVillage, cacheFile, { jhcis, cloud -> jhcis.name == cloud.name }) {
            getLogger(this).info { "Create new village ${it.toJson()}" }
            addAll(villageApi.toCloud(it, false))
            save()
        }
    }
}

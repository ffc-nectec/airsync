package ffc.airsync.api.village

import ffc.airsync.Main
import ffc.airsync.utils.checkDataUpdate
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
    val localVillage = arrayListOf<Village>().apply {
        addAll(load())
    }

    val jhcisVillage = localVillage.getVillage()
    if (localVillage.isEmpty()) {
        addAll(villageApi.toCloud(jhcisVillage))
        save()
    } else {
        val cloudVillage = villageApi.get()

        checkDataUpdate(jhcisVillage, cloudVillage, { jhcis, cloud -> jhcis.name == cloud.name }) {
            getLogger(this).info { "Update new village ${it.toJson()}" }
            val putVillage = villageApi.toCloud(it)
            localVillage.addAll(putVillage)
            save()
        }
        addAll(localVillage)
    }
}

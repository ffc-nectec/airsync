package ffc.airsync.api.village

import ffc.airsync.Main
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.airsync.villageApi
import ffc.airsync.villages
import ffc.entity.Village

val VILLAGELOOKUP = { jVillageId: String ->
    villages.find { it.link!!.keys["villcode"].toString() == jVillageId }
}

fun List<Village>.getVillage() = Main.instant.dao.getVillage()

fun ArrayList<Village>.initSync() {
    val localVillage = arrayListOf<Village>().apply {
        addAll(load())
    }

    if (localVillage.isEmpty()) {
        val getVillage = localVillage.getVillage()
        addAll(villageApi.toCloud(getVillage))
        save()
    } else {
        addAll(localVillage)
    }
}

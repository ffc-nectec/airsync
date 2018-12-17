package ffc.airsync.api.village

import ffc.airsync.Main
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.Village

fun Village.get() = Main.instant.dao.getVillage()

fun ArrayList<Village>.initSync() {
    val localVillage = arrayListOf<Village>().apply {
        addAll(load())
    }

    if (localVillage.isEmpty()) {
        addAll(villageApi.toCloud(localVillage))
        save()
    } else {
        addAll(localVillage)
    }
}

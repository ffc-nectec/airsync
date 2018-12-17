package ffc.airsync.api.village

import ffc.entity.Village

interface VillageApi {
    fun toCloud(villages: List<Village>): List<Village>
    fun editCloud(village: Village): Village
}

val villageApi: VillageApi by lazy { RetofitVillageApi() }
val villages = arrayListOf<Village>()

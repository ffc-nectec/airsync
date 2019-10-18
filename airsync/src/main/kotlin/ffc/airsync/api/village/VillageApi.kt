package ffc.airsync.api.village

import ffc.entity.Village

interface VillageApi {
    fun toCloud(villages: List<Village>, clearCloud: Boolean = true): List<Village>
    fun editCloud(village: Village): Village
    fun get(): List<Village>
}

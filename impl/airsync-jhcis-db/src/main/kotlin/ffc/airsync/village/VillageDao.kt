package ffc.airsync.village

import ffc.entity.Village

interface VillageDao {
    fun get(): List<Village>
}

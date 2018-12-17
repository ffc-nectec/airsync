package ffc.airsync.chronic

import ffc.entity.healthcare.Chronic

interface ChronicDao {
    fun get(): List<Chronic>
}

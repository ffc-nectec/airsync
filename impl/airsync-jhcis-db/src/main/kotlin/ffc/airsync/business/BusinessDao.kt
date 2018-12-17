package ffc.airsync.business

import ffc.entity.place.Business

interface BusinessDao {
    fun get(): List<Business>
}

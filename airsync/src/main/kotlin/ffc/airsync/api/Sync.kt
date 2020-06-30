package ffc.airsync.api

import ffc.entity.Entity

interface Sync {
    /**
     * @return รายการที่มีการ Sync
     */
    fun sync(): List<Entity>?
}

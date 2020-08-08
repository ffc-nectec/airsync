package ffc.airsync.api.person

import ffc.airsync.api.Sync
import ffc.entity.Entity
import ffc.entity.Person

class SyncPerson : Sync {
    override fun sync(force: Boolean): List<Entity> {
        TODO("Not yet implemented")
    }

    fun prePersonProcess(): List<Person> {
        return Person().gets()
    }
}

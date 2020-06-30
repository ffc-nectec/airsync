package ffc.airsync.api.person

import ffc.airsync.api.Sync
import ffc.airsync.api.chronic.Chronics
import ffc.entity.Entity
import ffc.entity.Person
import ffc.entity.healthcare.Chronic
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SyncPerson : Sync {
    override fun sync(): List<Entity> {
        TODO("Not yet implemented")
    }

    fun prePersonProcess(): List<Person> {
        var jhcisDbPerson: List<Person> = emptyList()
        var chronic: List<Chronic> = emptyList()
        runBlocking {
            launch { jhcisDbPerson = Person().gets() }
            launch { chronic = Chronics() }
        }
        jhcisDbPerson.mapChronic(chronic)
        return jhcisDbPerson
    }
}

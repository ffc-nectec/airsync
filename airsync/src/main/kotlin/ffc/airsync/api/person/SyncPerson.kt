package ffc.airsync.api.person

import ffc.airsync.api.Sync
import ffc.airsync.api.chronic.Chronics
import ffc.entity.Person

class SyncPerson() : Sync {
    override fun sync() {
        //TODO รอพัฒนาเพิ่ม ยังไงเพราะจำเป็นต้องผ่าน process ของบ้านด้วย
    }

    fun prePersonProcess(): List<Person> {
        val jhcisDbPerson = Person().gets()
        jhcisDbPerson.mapChronic(Chronics())
        return jhcisDbPerson
    }
}

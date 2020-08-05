package ffc.airsync.api.tag

import ffc.airsync.persons
import ffc.entity.Person
import ffc.entity.place.House

/**
 * ประมวลผล tags ต้องทำหลังจาก sync ข้อมูลขึ้น cloud แล้ว
 * เพราะจำเป็นต้องใช้ id จริงในการ update ข้อมูล
 */
class Level1TagProcess(persons: List<Person>, houses: List<House>, private val func: UpdateData) : TagProcess {

    interface UpdateData {
        fun updateHouse(house: House)
        fun updatePerson(person: Person)
    }

    val houseCacheSearch = houses.map { house ->
        val pcuCode = house.link!!.keys["pcucode"]!!.toString()
        val hCode = house.link!!.keys["hcode"]!!.toString()
        "$pcuCode:$hCode" to house
    }.toMap().toSortedMap()

    override fun process() {
        persons.forEach { person ->
            chronic(person)
            disableTag(person)
        }
    }

    private fun chronic(person: Person) {
        ChronicTag().run(person) {
            person.tags.add("chronic")
            func.updatePerson(person)
            houseCacheSearch["${person.pcuCode()}:${person.hCode()}"]?.let {
                it.tags.add("chronic")
                func.updateHouse(it)
            }
        }
    }

    private fun disableTag(person: Person) {
        DisableTag().run(person) {
            person.tags.add("disable")
            func.updatePerson(person)
            houseCacheSearch["${person.pcuCode()}:${person.hCode()}"]?.let {
                it.tags.add("disable")
                func.updateHouse(it)
            }
        }
    }

    private fun Person.pcuCode() = link!!.keys["pcucodeperson"]!!.toString()
    private fun Person.hCode() = link!!.keys["hcode"]!!.toString()
}

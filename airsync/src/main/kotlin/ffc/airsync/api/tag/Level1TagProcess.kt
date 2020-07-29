package ffc.airsync.api.tag

import ffc.airsync.persons
import ffc.entity.Person
import ffc.entity.place.House

class Level1TagProcess(persons: List<Person>, houses: List<House>) : TagProcess {
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
            houseCacheSearch["${person.pcuCode()}:${person.hCode()}"]?.tags?.add("chronic")
        }
    }

    private fun disableTag(person: Person) {
        DisableTag().run(person) {
            person.tags.add("disable")
            houseCacheSearch["${person.pcuCode()}:${person.hCode()}"]?.tags?.add("disable")
        }
    }

    private fun Person.pcuCode() = link!!.keys["pcucodeperson"]!!.toString()
    private fun Person.hCode() = link!!.keys["hcode"]!!.toString()
}

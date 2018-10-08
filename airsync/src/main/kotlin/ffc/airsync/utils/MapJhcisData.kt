package ffc.airsync.utils

import ffc.entity.House
import ffc.entity.Person
import ffc.entity.healthcare.Chronic

fun mapChronicToPerson(
    personFromDb: List<Person>,
    chronic: List<Chronic>
) {
    personFromDb.forEach {
        if (it.link == null) false

        val personPid = it.link!!.keys["pid"] as String
        if (personPid.isBlank()) false

        val chronicPerson = chronic.filter {
            if (it.link == null) false

            val chronicPid = it.link!!.keys["pid"] as String
            if (chronicPid.isBlank()) false

            (chronicPid == personPid)
        }

        if (chronicPerson.isEmpty()) false

        it.chronics.addAll(chronicPerson)
    }
}

fun findPersonInHouse(person: List<Person>, hcode: String): List<Person> {
    return person.filter {
        (it.link!!.keys["hcode"] as String).trim() == hcode
    }
}

fun checkChronicInHouse(house: List<House>) {
    house.forEach {
        val hcode = it.link!!.keys["hcode"] as String
        val persons = Person().gets()

        if (hcode.isNotEmpty() && hcode != "1") {
            val person = findPersonInHouse(persons, hcode)

            val personChronic = person.find {
                it.haveChronic
            }
            if (personChronic != null)
                it.haveChronic = true
        }
    }
}

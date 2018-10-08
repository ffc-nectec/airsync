package ffc.airsync.utils

import ffc.entity.House
import ffc.entity.Person
import ffc.entity.healthcare.Chronic

fun List<Person>.mapChronic(chronic: List<Chronic>) {
    mapChronicToPerson(this, chronic)
}

fun List<Person>.findByHouseCode(hcode: String): List<Person> {
    return findPersonInHouse(this, hcode)
}

fun House.findPerson(persons: List<Person>): List<Person> {
    val houseCode = (link!!.keys["hcode"] as String)
    return findPersonInHouse(persons, houseCode)
}

fun List<House>.chronicCalculate(persons: List<Person>) {
    checkChronicInHouse(persons, this)
}

private fun mapChronicToPerson(
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

private fun findPersonInHouse(person: List<Person>, hcode: String): List<Person> {
    return person.filter {
        (it.link!!.keys["hcode"] as String).trim() == hcode
    }
}

private fun checkChronicInHouse(persons: List<Person>, house: List<House>) {
    house.forEach {
        val hcode = it.link!!.keys["hcode"] as String

        if (hcode.isNotEmpty() && hcode != "1") {
            val person = persons.findByHouseCode(hcode)

            val personChronic = person.find {
                it.haveChronic
            }
            if (personChronic != null)
                it.haveChronic = true
        }
    }
}

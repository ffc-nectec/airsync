package ffc.airsync.api.person

import ffc.airsync.Main
import ffc.airsync.db.DatabaseDao
import ffc.airsync.personApi
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.Person
import ffc.entity.healthcare.Chronic
import ffc.entity.place.House

fun Person.gets(dao: DatabaseDao = Main.instant.createDatabaseDao()): List<Person> {
    val persons = dao.getPerson()
    val chronic = dao.getChronic()

    return mapChronics(persons, chronic)
}

fun ArrayList<Person>.initSync(houseFromCloud: List<House>, personIsChronic: List<Person>) {
    val localPersons = arrayListOf<Person>().apply {
        addAll(load())
    }

    if (localPersons.isEmpty()) {

        personIsChronic.mapHouseId(houseFromCloud)
        localPersons.addAll(personIsChronic)

        addAll(personApi.putPerson(localPersons))
        save()
    } else {
        addAll(localPersons)
    }
}

private fun List<Person>.mapHouseId(houseFromCloud: List<House>) {
    forEach {
        if (it.link != null) {
            val hcodePerson = (it.link!!.keys["hcode"] as String)
            val house = houseFromCloud.find { it.link?.keys?.get("hcode") as String == hcodePerson }
            if (house != null)
                it.houseId = house.id
            else
                it.houseId = ""
        }
    }
}

fun List<Person>.mapChronic(chronic: List<Chronic>) {
    forEach {
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

fun List<Person>.findByHouseCode(hcode: String): List<Person> {
    return filter {
        (it.link!!.keys["hcode"] as String).trim() == hcode
    }
}

private fun mapChronics(persons: List<Person>, chronics: List<Chronic>): List<Person> {
    persons.forEach { person ->
        person.chronics.addAll(chronics.filter {
            it.link!!.keys["pid"] == person.link!!.keys["pid"]
        })
    }
    return persons
}

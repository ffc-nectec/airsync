package ffc.airsync.api.person

import ffc.airsync.Main
import ffc.airsync.api.chronic.gets
import ffc.airsync.db.DatabaseDao
import ffc.airsync.personApi
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.Person
import ffc.entity.healthcare.Chronic
import ffc.entity.healthcare.Disease

fun Person.gets(dao: DatabaseDao = Main.instant.createDatabaseDao()): List<Person> {
    val persons = dao.getPerson()
    val chronic = dao.getChronic()

    return mapChronics(persons, chronic)
}

fun ArrayList<Person>.initSync() {
    val localPersons = arrayListOf<Person>().apply {
        addAll(load())
    }

    if (localPersons.isEmpty()) {
        val personFromDb = Person().gets()
        val chronic = Chronic(Disease("", "", "")).gets()

        personFromDb.mapChronic(chronic)

        localPersons.addAll(personFromDb)
        addAll(personApi.putPerson(localPersons))
        save()
    } else {
        addAll(localPersons)
    }
}

fun List<Person>.mapChronic(chronic: List<Chronic>) {
    mapChronicToPerson(this, chronic)
}

fun List<Person>.findByHouseCode(hcode: String): List<Person> {
    return findPersonInHouse(this, hcode)
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

private fun mapChronics(persons: List<Person>, chronics: List<Chronic>): List<Person> {
    persons.forEach { person ->
        person.chronics.addAll(chronics.filter {
            it.link!!.keys["pid"] == person.link!!.keys["pid"]
        })
    }
    return persons
}

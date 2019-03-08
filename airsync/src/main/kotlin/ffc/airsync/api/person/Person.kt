package ffc.airsync.api.person

import ffc.airsync.Main
import ffc.airsync.api.icd10.icd10Api
import ffc.airsync.db.DatabaseDao
import ffc.airsync.printDebug
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.Person
import ffc.entity.healthcare.Chronic
import ffc.entity.healthcare.Disease
import ffc.entity.place.House

fun Person.gets(dao: DatabaseDao = Main.instant.dao): List<Person> {
    val persons = dao.getPerson()
    val chronic = dao.getChronic()

    return mapChronics(persons, chronic)
}

fun ArrayList<Person>.initSync(
    houseFromCloud: List<House>,
    personIsChronic: List<Person>,
    progressCallback: (Int) -> Unit
) {
    val localPersons = arrayListOf<Person>().apply {
        addAll(load())
    }

    if (localPersons.isEmpty()) {

        personIsChronic.mapHouseId(houseFromCloud, progressCallback)
        localPersons.addAll(personIsChronic)
        mapDeath(personIsChronic, progressCallback)
        addAll(personApi.putPerson(localPersons, progressCallback))
        save()
    } else {
        addAll(localPersons)
    }
}

private fun List<Person>.mapHouseId(
    houseFromCloud: List<House>,
    progressCallback: (Int) -> Unit
) {
    val sizeOfLoop = size
    forEachIndexed { index, it ->
        if (it.link != null) {
            val hcodePerson = (it.link!!.keys["hcode"] as String)
            val house = houseFromCloud.find { it.link?.keys?.get("hcode") as String == hcodePerson }
            if (house != null)
                it.houseId = house.id
            else
                it.houseId = ""
        }
        progressCallback((index * 30) / sizeOfLoop)
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

private fun mapDeath(persons: List<Person>, progressCallback: (Int) -> Unit) {
    val lookUpIcd10 = { icd10: String -> icd10Api.lookup(icd10) }

    val sizeOfLoop = persons.size
    persons.forEachIndexed { index, person ->
        val death = person.death
        if (death != null) {
            printDebug("Dead ${person.name}")
            val diseaseList = arrayListOf<Disease>()
            death.causes.forEach {
                diseaseList.add(lookUpIcd10(it.name))
            }
            person.death = Person.Death(death.date, diseaseList.toList())
        }
        progressCallback(((index * 20) / sizeOfLoop) + 30)
    }
}

package ffc.airsync.api.person

import ffc.airsync.Main
import ffc.airsync.db.DatabaseDao
import ffc.airsync.personApi
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.Person
import ffc.entity.Person.Relate.Child
import ffc.entity.Person.Relate.Father
import ffc.entity.Person.Relate.Married
import ffc.entity.Person.Relate.Mother
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

fun List<Person>.syncRelation() {

    val houseMap = HashMap<String, ArrayList<Person>>()

    forEach { person ->

        if (person.link?.keys?.get("hcode") == "1") return@forEach

        val father = person.link?.keys?.get("father")
        val mother = person.link?.keys?.get("mother")
        val mate = person.link?.keys?.get("mate")

        person.link?.keys?.get("fatherid")?.let { personId ->
            find { search(it, personId) }?.let {
                person.addRelationship(Pair(Father, it))
                it.addRelationship(Pair(Child, person))
            }
        }

        person.link?.keys?.get("motherid")?.let { personId ->
            find { search(it, personId) }?.let {
                person.addRelationship(Pair(Mother, it))
                it.addRelationship(Pair(Child, person))
            }
        }

        person.link?.keys?.get("mateid")?.let { personId ->
            find { search(it, personId) }?.let {
                person.addRelationship(Pair(Married, it))
                it.addRelationship(Pair(Married, person))
            }
        }

        val personInHouse = filter { it.houseId == person.houseId }
    }
}

private fun search(it: Person, personId: Any) = it.identities.find { it.id == personId } != null

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

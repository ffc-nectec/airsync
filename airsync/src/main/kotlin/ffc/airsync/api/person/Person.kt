package ffc.airsync.api.person

import ffc.airsync.Main
import ffc.airsync.db.DatabaseDao
import ffc.airsync.icd10Api
import ffc.airsync.personApi
import ffc.airsync.utils.checkNewDataCreate
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.Person
import ffc.entity.gson.toJson
import ffc.entity.healthcare.Chronic
import ffc.entity.healthcare.Disease
import ffc.entity.place.House

private interface PersonUtil

private val logger by lazy { getLogger(PersonUtil::class) }

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
    val cacheFile = arrayListOf<Person>().apply {
        logger.trace("initSync load person.")
        addAll(load())
    }

    if (cacheFile.isEmpty()) {
        logger.info("Load person from databse.")
        createPersonOnCloud(personIsChronic, houseFromCloud, progressCallback)
    } else {
        logger.info("Load person from airsync file.")
        addAll(cacheFile)
        checkNewDataCreate(personIsChronic, cacheFile, { jhcis, cloud ->
            var out = false
            jhcis.identities.forEach { jId ->
                cloud.identities.forEach { cId ->
                    if (jId == cId) out = true
                }
            }
            out
        }) {
            getLogger(this).info { "Create new person ${it.toJson()}" }
            createPersonOnCloud(it, houseFromCloud, progressCallback)
        }
    }
    progressCallback(100)
}

private fun ArrayList<Person>.createPersonOnCloud(
    personIsChronic: List<Person>,
    houseFromCloud: List<House>,
    progressCallback: (Int) -> Unit
) {
    personIsChronic.mapHouseId(houseFromCloud, progressCallback)
    mapDeath(personIsChronic, progressCallback)
    addAll(personApi.putPerson(personIsChronic, progressCallback))
    save()
}

private fun List<Person>.mapHouseId(
    houseFromCloud: List<House>,
    progressCallback: (Int) -> Unit
) {
    val sizeOfLoop = size
    forEachIndexed { index, it ->
        if (it.link != null) {
            val hcodePerson = (it.link!!.keys["hcode"] as String)
            val house = houseFromCloud.find {
                val hcode = (it.link?.keys?.get("hcode") ?: "") as String
                if (hcode.isNotBlank())
                    hcode == hcodePerson
                else
                    false
            }
            if (house != null)
                it.houseId = house.id
            else
                it.houseId = ""
        }
        progressCallback((index * 30) / sizeOfLoop)
    }
}

/**
 * นำคนมาแมพหา chronic
 */
fun List<Person>.mapChronic(chronic: List<Chronic>) {
    forEach {
        if (it.link != null) {
            val personPid = it.link!!.keys["pid"] as String
            if (personPid.isNotBlank()) {
                val chronicPerson = chronic.filter {
                    if (it.link != null) {
                        val chronicPid = (it.link!!.keys["pid"] ?: "") as String
                        (chronicPid == personPid)
                    } else
                        false
                }
                if (chronicPerson.isNotEmpty())
                    it.chronics.addAll(chronicPerson)
            }
        }
    }
}

fun List<Person>.findByHouseCode(hcode: String): List<Person> {
    return filter {
        (it.link!!.keys["hcode"] as String).trim() == hcode
    }
}

private fun mapChronics(persons: List<Person>, chronics: List<Chronic>): List<Person> {
    logger.trace("Map chronics to person")
    persons.forEach { person ->
        person.chronics.addAll(chronics.filter {
            it.link!!.keys["pid"] == person.link!!.keys["pid"]
        })
    }
    return persons
}

private fun mapDeath(persons: List<Person>, progressCallback: (Int) -> Unit) {
    logger.trace("Process person death.")
    val lookUpIcd10 = { icd10: String -> icd10Api.lookup(icd10) }

    val sizeOfLoop = persons.size
    persons.forEachIndexed { index, person ->
        val death = person.death
        if (death != null) {
            val diseaseList = arrayListOf<Disease>()
            death.causes.forEach {
                diseaseList.add(lookUpIcd10(it.name))
            }
            person.death = Person.Death(death.date, diseaseList.toList())
            logger.debug("Dead ${person.name} ${person.death?.causes?.size}")
        }
        progressCallback(((index * 20) / sizeOfLoop) + 30)
    }
}

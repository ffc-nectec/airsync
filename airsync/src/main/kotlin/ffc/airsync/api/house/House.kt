package ffc.airsync.api.house

import ffc.airsync.Main
import ffc.airsync.api.person.findByHouseCode
import ffc.airsync.db.DatabaseDao
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.Person
import ffc.entity.place.House

fun House.gets(where: String = "", dao: DatabaseDao = Main.instant.createDatabaseDao()): List<House> {
    return if (where.isBlank()) dao.getHouse() else dao.getHouse(where)
}

fun ArrayList<House>.initSync(person: List<Person>) {
    val localHouses = arrayListOf<House>().apply {
        addAll(load())
    }

    if (localHouses.isEmpty()) {
        val house = House().gets()
        house.chronicCalculate(person)
        localHouses.addAll(house)

        addAll(houseApi.putHouse(localHouses))
        save()
    } else {
        addAll(localHouses)
    }
}

fun List<House>.chronicCalculate(persons: List<Person>) {
    checkChronicInHouse(persons, this)
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

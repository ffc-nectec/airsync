package ffc.airsync.api.house

import ffc.airsync.Main
import ffc.airsync.api.person.findByHouseCode
import ffc.airsync.db.DatabaseDao
import ffc.entity.House
import ffc.entity.Person

fun List<House>.chronicCalculate(persons: List<Person>) {
    checkChronicInHouse(persons, this)
}

fun House.gets(where: String = "", dao: DatabaseDao = Main.instant.createDatabaseDao()): List<House> {
    return if (where.isBlank()) dao.getHouse() else dao.getHouse(where)
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

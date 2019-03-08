package ffc.airsync.api.house

import ffc.airsync.Main
import ffc.airsync.api.person.findByHouseCode
import ffc.airsync.api.village.VILLAGELOOKUP
import ffc.airsync.db.DatabaseDao
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.Person
import ffc.entity.place.House

fun House.gets(where: String = "", dao: DatabaseDao = Main.instant.dao): List<House> {
    return if (where.isBlank()) dao.getHouse(VILLAGELOOKUP) else dao.getHouse(VILLAGELOOKUP, where)
}

fun ArrayList<House>.initSync(person: List<Person>, progressCallback: (Int) -> Unit) {
    val localHouses = arrayListOf<House>().apply {
        addAll(load())
    }

    if (localHouses.isEmpty()) {
        val house = House().gets()
        checkChronicInHouse(person, house, progressCallback)
        localHouses.addAll(house)

        addAll(houseApi.putHouse(localHouses, progressCallback))
        save()
    } else {
        addAll(localHouses)
    }
}

private fun checkChronicInHouse(persons: List<Person>, house: List<House>, progressCallback: (Int) -> Unit) {
    val houseSize = house.size
    house.forEachIndexed { index, it ->
        val hcode = it.link!!.keys["hcode"] as String

        if (hcode.isNotEmpty() && hcode != "1") {
            val person = persons.findByHouseCode(hcode)

            val personChronic = person.find {
                it.haveChronic
            }
            if (personChronic != null)
                it.haveChronic = true
        }
        progressCallback((index * 50) / houseSize)
    }
}

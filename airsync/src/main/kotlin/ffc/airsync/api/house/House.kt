/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package ffc.airsync.api.house

import ffc.airsync.Main
import ffc.airsync.api.person.findByHouseCode
import ffc.airsync.api.village.VILLAGELOOKUP
import ffc.airsync.db.DatabaseDao
import ffc.airsync.houseApi
import ffc.airsync.utils.checkNewDataCreate
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.Person
import ffc.entity.gson.toJson
import ffc.entity.place.House

fun House.gets(where: String = "", dao: DatabaseDao = Main.instant.dao): List<House> {
    return if (where.isBlank()) dao.getHouse(VILLAGELOOKUP) else dao.getHouse(VILLAGELOOKUP, where)
}

fun ArrayList<House>.initSync(person: List<Person>, progressCallback: (Int) -> Unit) {
    this.lock {
        val cacheFile = arrayListOf<House>().apply {
            addAll(load())
        }
        val jhcisHouse = House().gets()
        clear()
        if (cacheFile.isEmpty()) {
            createHouseOnCloud(person, jhcisHouse, progressCallback)
        } else {
            addAll(cacheFile)
            checkNewDataCreate(jhcisHouse, cacheFile, { jhcis, cloud ->
                val pcuCheck = runCatching { jhcis.link!!.keys["pcucode"] == cloud.link!!.keys["pcucode"] }
                val hcodeCheck = runCatching { jhcis.link!!.keys["hcode"] == cloud.link!!.keys["hcode"] }
                val keyCheck =
                    if (pcuCheck.isSuccess && hcodeCheck.isSuccess)
                        pcuCheck.getOrThrow() && hcodeCheck.getOrThrow()
                    else false

                keyCheck
            }) {
                getLogger(this).info { "Create new house ${it.toJson()}" }
                createHouseOnCloud(person, it, progressCallback, false)
            }
        }
        save()
        progressCallback(100)
    }
}

fun ArrayList<House>.updateLocalData(list: List<House>) {
    list.forEach { updateItem ->
        val house = find { it.id == updateItem.id }
        if (house != null) {
            removeIf { it.id == house.id }
            add(house)
        } else
            getLogger(this).info { "House update map null." }
    }
    save()
}

private fun ArrayList<House>.createHouseOnCloud(
    person: List<Person>,
    jhcisHouse: List<House>,
    progressCallback: (Int) -> Unit,
    clearCloud: Boolean = true
) {
    checkChronicInHouse(person, jhcisHouse, progressCallback)
    addAll(houseApi.createHouse(jhcisHouse, progressCallback, clearCloud))
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
        if (houseSize != 0)
            progressCallback((index * 50) / houseSize)
    }
}

private const val houseLock = "lock"

fun List<House>.lock(f: () -> Unit) {
    synchronized(houseLock) {
        f()
    }
}

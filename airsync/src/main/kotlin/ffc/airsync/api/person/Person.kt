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

package ffc.airsync.api.person

import ffc.airsync.Main
import ffc.airsync.api.village.initSync
import ffc.airsync.db.DatabaseDao
import ffc.airsync.houseManage
import ffc.airsync.lookupDisease
import ffc.airsync.personApi
import ffc.airsync.persons
import ffc.airsync.utils.checkNewDataCreate
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.airsync.villages
import ffc.entity.Person
import ffc.entity.gson.toJson
import ffc.entity.place.House

private interface PersonUtil

private val logger by lazy { getLogger(PersonUtil::class) }

fun Person.gets(dao: DatabaseDao = Main.instant.dao): List<Person> {
    return getPersons(dao)
}

fun getPersons(dao: DatabaseDao = Main.instant.dao): List<Person> {
    return dao.getPerson(lookupDisease)
}

fun ArrayList<Person>.initSync(
    houseFromCloud: List<House>,
    personIsChronic: List<Person>,
    progressCallback: (Int) -> Unit
) {
    this.lock {
        val cacheFile = arrayListOf<Person>().apply {
            logger.trace("initSync load person.")
            addAll(load())
        }
        clear()
        if (cacheFile.isEmpty()) {
            logger.info("Load person from databse.")
            createPersonOnCloud(personIsChronic, houseFromCloud, progressCallback)
        } else {
            logger.info("Load person from airsync file.")
            addAll(cacheFile)
            checkNewDataCreate(personIsChronic, cacheFile, { jhcis, cloud ->
                val pcuCheck = runCatching { jhcis.link!!.keys["pcucodeperson"] == cloud.link!!.keys["pcucodeperson"] }
                val pidCheck = runCatching { jhcis.link!!.keys["pid"] == cloud.link!!.keys["pid"] }

                if (pcuCheck.isSuccess && pidCheck.isSuccess)
                    pcuCheck.getOrThrow() && pidCheck.getOrThrow()
                else false
            }) {
                getLogger(this).info { "Create new person ${it.toJson()}" }
                createPersonOnCloud(it, houseFromCloud, progressCallback, false)
            }
        }
        save()
        progressCallback(100)
    }
}

private fun ArrayList<Person>.createPersonOnCloud(
    personIsChronic: List<Person>,
    houseFromCloud: List<House>,
    progressCallback: (Int) -> Unit,
    clearCloud: Boolean = true
) {
    // TODO Optimize ได้มีการ loop ซ้ำแบบเดียวกัน 2 function หรือจะใช้แบบ async แยกทำก็ได้
    personIsChronic.mapHouseId(houseFromCloud, progressCallback)
    addAll(personApi.createPerson(personIsChronic, progressCallback, clearCloud))
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
        if (sizeOfLoop != 0)
            progressCallback((index * 30) / sizeOfLoop)
    }
}

fun List<Person>.findByHouseCode(hcode: String): List<Person> {
    return filter {
        (it.link!!.keys["hcode"] as String).trim() == hcode
    }
}

private const val personLock = "lock"

fun List<Person>.lock(f: () -> Unit) {
    synchronized(personLock) {
        f()
    }
}

fun findPersonId(pid: String): String {
    val id = persons.find { it.link!!.keys["pid"] == pid }?.id
    return if (id == null) {
        val syncPerson = SyncPerson()
        val jhcisDbPerson = syncPerson.prePersonProcess()
        villages.initSync()
        houseManage.sync()
        persons.initSync(houseManage.cloud, jhcisDbPerson) {}
        persons.find { it.link!!.keys["pid"] == pid }!!.id
    } else
        id
}

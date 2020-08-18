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

package ffc.airsync.api.tag

import ffc.entity.Entity
import ffc.entity.Person
import ffc.entity.place.House

/**
 * ประมวลผล tags ต้องทำหลังจาก sync ข้อมูลขึ้น cloud แล้ว
 * เพราะจำเป็นต้องใช้ id จริงในการ update ข้อมูล
 */
class Level1TagProcess(
    private val persons: List<Person>,
    private val houses: List<House>,
    private val func: () -> UpdateData
) : TagProcess {

    interface UpdateData {
        fun updateHouse(house: House)
        fun updatePerson(person: Person)
    }

    val houseCacheSearch = houses.map { house ->
        val pcuCode = house.link!!.keys["pcucode"]!!.toString()
        val hCode = house.link!!.keys["hcode"]!!.toString()
        "$pcuCode:$hCode" to house
    }.toMap().toSortedMap()

    override fun process() {
        persons.forEach { person ->
            chronic(person)
            disableTag(person)
        }
    }

    private fun chronic(person: Person) {
        val tagName = "chronic"
        ChronicTag().run(person) {
            if (it.addTag(tagName)) func().updatePerson(it)
            val house = houseCacheSearch["${person.pcuCode()}:${person.hCode()}"]
            if (house.addTag(tagName))
                func().updateHouse(house!!)
        }
    }

    private fun Entity?.addTag(tagName: String): Boolean {
        if (this == null) return false
        if (tags.contains(tagName)) return false
        tags.add(tagName)
        return true
    }

    private fun disableTag(person: Person) {
        val tagName = "disable"
        DisableTag().run(person) {
            if (it.addTag(tagName)) func().updatePerson(it)
            val house = houseCacheSearch["${person.pcuCode()}:${person.hCode()}"]
            if (house.addTag(tagName))
                func().updateHouse(house!!)
        }
    }

    private fun Person.pcuCode() = link!!.keys["pcucodeperson"]!!.toString()
    private fun Person.hCode() = link!!.keys["hcode"]!!.toString()
}

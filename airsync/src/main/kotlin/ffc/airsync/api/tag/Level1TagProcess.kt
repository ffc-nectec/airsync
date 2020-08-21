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
 * จะมี tag ที่เกี่ยวกับการวิเคราะห์โรค จะอยู่ที่ ffc/airsync/api/analyzer/Analyzer.kt
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

    val houseCacheSearch = houses.map {
        it.id to it
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
            val personUpdate = it.addTag(tagName)
            if (personUpdate != null) func().updatePerson(personUpdate)

            if (!it.isDead) {
                val house = houseCacheSearch[person.houseId]
                val houseUpdate = house.addTag(tagName)
                if (houseUpdate != null) func().updateHouse(houseUpdate)
            }
        }
    }

    private fun <T : Entity> T?.addTag(tagName: String): T? {
        if (this == null) return null
        if (tags.contains(tagName)) return null
        tags.add(tagName)
        return this
    }

    private fun <T : Entity> T?.removeTag(tagName: String): T? {
        if (this == null) return null
        return if (tags.contains(tagName)) {
            tags.remove(tagName)
            this
        } else null
    }

    private fun disableTag(person: Person) {
        val tagName = "disable"
        DisableTag().run(person) {
            val personUpdate = it.addTag(tagName)
            if (personUpdate != null) func().updatePerson(personUpdate)

            if (!it.isDead) {
                val house = houseCacheSearch[person.houseId]
                val houseUpdate = house.addTag(tagName)
                if (houseUpdate != null) func().updateHouse(houseUpdate)
            }
        }
    }
}

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

package ffc.airsync.api.genogram

import ffc.airsync.api.genogram.lib.GENOSEX
import ffc.airsync.api.genogram.lib.PersonDetailInterface
import ffc.airsync.utils.getLogger
import ffc.entity.Person
import ffc.entity.Person.Relate
import ffc.entity.Person.Relate.Divorced
import ffc.entity.Person.Relate.Father
import ffc.entity.Person.Relate.Married
import ffc.entity.Person.Relate.Mother
import ffc.entity.THAI_CITIZEN_ID
import ffc.entity.gson.toJson

class FFCAdapterPersonDetailInterface(persons: List<Person>) : PersonDetailInterface<Person> {
    private val util = Util()
    private val idCardMapCache = persons.map { getIdCard(it) to it }.toMap().toSortedMap()
    private val idMapCache = persons.map { it.id to it }.toMap().toSortedMap()
    private val logger = getLogger(this)

    override fun getAge(person: Person): Int? {
        return person.age
    }

    override fun getSex(person: Person): GENOSEX? {
        return when (person.sex) {
            Person.Sex.FEMALE -> GENOSEX.FEMALE
            Person.Sex.MALE -> GENOSEX.MALE
            else -> null
        }
    }

    override fun getPcuCode(person: Person): String? {
        return person.link?.keys?.get("pcucodeperson")?.toString()
    }

    override fun getHouseNumber(person: Person): String? {
        return person.link?.keys?.get("hcode")?.toString()
    }

    override fun getIdCard(person: Person): String {
        return person.identities.find { it.type == THAI_CITIZEN_ID }!!.id
    }

    override fun getFatherInRelation(person: Person): Person? {
        person.relationships.find { it.relate == Father }?.let { father ->
            val person1 = idMapCache[father.id]
            if (person1 == null) logger.warn { "ค้นหาไอดีบัตรพ่อไม่เจอ ${father.id}" }
            return person1
        }
        return null
    }

    override fun setFather(person: Person, fatherIdCard: String) {
        val person1 = idCardMapCache[fatherIdCard]
        if (person1 == null) logger.warn { "ค้นหาไอดีบัตรพ่อไม่เจอ $fatherIdCard" }
        person1?.let { util.`สร้างความสัมพันธ์พ่อ`(person, it) }
    }

    override fun getMotherInRelation(person: Person): Person? {
        person.relationships.find { it.relate == Mother }?.let { mother ->
            val person1 = idMapCache[mother.id]
            if (person1 == null) logger.warn { "ค้นหาไอดีบัตรแม่ไม่เจอ ${mother.id}" }
            return person1
        }
        return null
    }

    override fun setMother(person: Person, motherIdCard: String) {
        val person1 = idCardMapCache[motherIdCard]
        if (person1 == null) logger.warn { "ค้นหาไอดีบัตรแม่ไม่เจอ $motherIdCard" }
        person1?.let {
            util.`สร้างความสัมพันธ์แม่`(person, it)
        }
    }

    override fun getMateInRelation(person: Person): List<Person> {
        person.relationships.filter { it.relate == Married || it.relate == Divorced }.let { mateRelationList ->
            val result = arrayListOf<Person>()
            mateRelationList.forEach { mate ->
                val person1 = idMapCache[mate.id]
                if (person1 == null) logger.warn { "ค้นหาไอดีบัตรแฟนไม่เจอ ${mate.id}" }
                person1?.let { result.add(it) }
            }
            return result.toList()
        }
    }

    private var countRelationError = 0
    var debugErrorRelation = hashSetOf<String>()
    override fun addMate(person: Person, mateIdCard: String) {
        val baseStatus = person.link?.keys?.get("marystatusth")?.toString()?.trim()
        val person1 = idCardMapCache[mateIdCard]
        if (person1 == null) logger.warn { "ค้นหาไอดีบัตรแฟนไม่เจอ $mateIdCard" }
        person1?.let {
            val mStatus = it.link?.keys?.get("marystatusth")?.toString()?.trim()
            val relation = marriedCondition(baseStatus, mateIdCard)
            try {
                person.addRelationship(relation to it)
            } catch (ex: java.lang.IllegalArgumentException) {
                countRelationError++
                val bPcucode = person.link?.keys?.get("pcucodeperson")?.toString()
                val basePid = person.link?.keys?.get("pid")?.toString()
                val baseName = person.name
                val mPcuCode = person1.link?.keys?.get("pcucodeperson")?.toString()
                val mbasePid = person1.link?.keys?.get("pid")?.toString()
                val mbaseName = person1.name
                debugErrorRelation.add("$baseStatus:$mStatus")
                logger.error { "count:$countRelationError base:marr=$baseStatus:$mStatus" }
                logger.error(ex) {
                    "count:$countRelationError " +
                            "คนหลัก $baseName pcucode:$bPcucode pid:$basePid " +
                            " คู่ครอง $mbaseName pcucode:$mPcuCode pid:$mbasePid " +
                            " Debug คนหลัก ${person.relationships.toJson()} " +
                            " Debug คนสัมพัน ${person1.relationships.toJson()}"
                }
            }
        }
    }

    private fun marriedCondition(first: String?, second: String?): Relate {
        return if (first != second) {
            val groupRelate = listOf(first, second)
            when {
                groupRelate.contains("หย่า") -> Divorced
                groupRelate.contains("แยก") -> Divorced
                else -> Married
            }
        } else {
            when (first) {
                "โสด", "คู่", "ไม่ทราบ" -> Married
                else -> Divorced
            }
        }
    }

    override fun getFirstName(person: Person): String {
        return person.firstname
    }

    override fun getLastName(person: Person): String {
        return person.lastname
    }

    override fun getFatherInformationId(person: Person): String? {
        return person.link?.keys?.get("fatherid")?.toString()
    }

    override fun getFatherFirstName(person: Person): String? {
        return person.link?.keys?.get("father")?.toString()?.getFirstAndLastName()?.first
    }

    override fun getFatherLastName(person: Person): String? {
        return person.link?.keys?.get("father")?.toString()?.getFirstAndLastName()?.second
    }

    override fun getMotherInformationId(person: Person): String? {
        return person.link?.keys?.get("motherid")?.toString()
    }

    override fun getMotherFirstName(person: Person): String? {
        return person.link?.keys?.get("mother")?.toString()?.getFirstAndLastName()?.first
    }

    override fun getMotherLastName(person: Person): String? {
        return person.link?.keys?.get("mother")?.toString()?.getFirstAndLastName()?.second
    }

    override fun getMateInformationId(person: Person): String? {
        return person.link?.keys?.get("mateid")?.toString()
    }

    override fun getMateFirstName(person: Person): String? {
        return person.link?.keys?.get("mate")?.toString()?.getFirstAndLastName()?.first
    }

    override fun getMateLastName(person: Person): String? {
        return person.link?.keys?.get("mate")?.toString()?.getFirstAndLastName()?.second
    }

    private fun String.getFirstAndLastName(): Pair<String?, String?> {
        val split = this.trim().split(" ")
        return if (split.size >= 2)
            split.firstOrNull().takeIf { !it.isNullOrBlank() } to split.lastOrNull()
                .takeIf { !it.isNullOrBlank() }
        else split.firstOrNull().takeIf { !it.isNullOrBlank() } to null
    }

    internal fun getFirstAndLastName(name: String): Pair<String?, String?> {
        return name.getFirstAndLastName()
    }
}

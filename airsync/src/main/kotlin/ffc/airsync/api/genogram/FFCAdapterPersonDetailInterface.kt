package ffc.airsync.api.genogram

import ffc.airsync.api.genogram.lib.GENOSEX
import ffc.airsync.api.genogram.lib.PersonDetailInterface
import ffc.entity.Person
import ffc.entity.Person.Relate.Father
import ffc.entity.Person.Relate.Married
import ffc.entity.Person.Relate.Mother
import ffc.entity.THAI_CITIZEN_ID

class FFCAdapterPersonDetailInterface(val persons: List<Person>) : PersonDetailInterface<Person> {
    private val util = Util()
    private val idCardMapCache = persons.map { getIdCard(it) to it }.toMap()
    private val idMapCache = persons.map { it.id to it }.toMap()

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
            return persons.find { it.id == father.id }
        }
        return null
    }

    override fun setFather(person: Person, fatherIdCard: String) {
        persons.find { getIdCard(it) == fatherIdCard }?.let {
            util.`สร้างความสัมพันธ์พ่อ`(person, it)
        }
    }

    override fun getMotherInRelation(person: Person): Person? {
        person.relationships.find { it.relate == Mother }?.let { mother ->
            return persons.find { it.id == mother.id }
        }
        return null
    }

    override fun setMother(person: Person, motherIdCard: String) {
        persons.find { getIdCard(it) == motherIdCard }?.let {
            util.`สร้างความสัมพันธ์แม่`(person, it)
        }
    }

    override fun getMateInRelation(person: Person): List<Person> {
        person.relationships.filter { it.relate == Married }.let { mate ->
            return persons.filter { p ->
                mate.find { it.id == p.id } != null
            }
        }
    }

    override fun addMate(person: Person, mateIdCard: String) {
        persons.find { getIdCard(it) == mateIdCard }?.let {
            person.addRelationship(Married to it)
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

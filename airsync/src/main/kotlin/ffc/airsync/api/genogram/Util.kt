package ffc.airsync.api.genogram

import ffc.airsync.utils.getLogger
import ffc.entity.Person
import ffc.entity.Person.Relate.Child
import ffc.entity.Person.Relate.Father
import ffc.entity.Person.Relate.Mother
import ffc.entity.Person.Sex.FEMALE
import ffc.entity.Person.Sex.MALE
import ffc.entity.Person.Sex.UNKNOWN

internal class Util {
    private val logger = getLogger(this)

    fun `สร้างความสัมพันธ์สามีภรรยา`(person: Person, it: Person) {
        if (person.`แฟนกันได้ไหม`(it))
            try {
                person.addRelationship(Pair(Person.Relate.Married, it))
                it.addRelationship(Pair(Person.Relate.Married, person))
            } catch (ex: java.lang.IllegalArgumentException) {
                logger.warn(ex) { "แฟน error ${ex.message}" }
            }
    }

    private fun Person.`แฟนกันได้ไหม`(person: Person): Boolean {
        if (sex == MALE && person.sex == MALE) return false
        if (sex == FEMALE && person.sex == FEMALE) return false
        return true
    }

    fun `สร้างความสัมพันธ์แม่`(child: Person, mother: Person) {
        if (mother.sex == FEMALE || mother.sex == UNKNOWN)
            try {
                val check = child.relationships.find { it.relate == Mother }
                if (check == null) {
                    child.addRelationship(Pair(Mother, mother))
                    mother.addRelationship(Pair(Child, child))
                }
            } catch (ex: java.lang.IllegalArgumentException) {
                logger.warn(ex) { "แม่ error ${ex.message}" }
            }
    }

    fun `สร้างความสัมพันธ์พ่อ`(child: Person, father: Person) {
        if (father.sex == MALE || father.sex == UNKNOWN)
            try {
                val check = child.relationships.find { it.relate == Father }
                if (check == null) {
                    child.addRelationship(Pair(Father, father))
                    father.addRelationship(Pair(Child, child))
                }
            } catch (ex: java.lang.IllegalArgumentException) {
                logger.warn(ex) { "พ่อ error ${child.id} ${father.id} ${ex.message}" }
            }
    }
}

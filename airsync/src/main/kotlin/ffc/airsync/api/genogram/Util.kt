package ffc.airsync.api.genogram

import ffc.entity.Person

internal class Util {

    fun `สร้างความสัมพันธ์สามีภรรยา`(person: Person, it: Person) {
        if (person.`แฟนกันได้ไหม`(it))
            try {
                person.addRelationship(Pair(Person.Relate.Married, it))
                it.addRelationship(Pair(Person.Relate.Married, person))
            } catch (ex: java.lang.IllegalArgumentException) {
            }
    }

    private fun Person.`แฟนกันได้ไหม`(person: Person): Boolean {
        if (sex == Person.Sex.MALE && person.sex == Person.Sex.FEMALE) return true
        if (sex == Person.Sex.FEMALE && person.sex == Person.Sex.MALE) return true
        return false
    }

    fun `สร้างความสัมพันธ์แม่`(child: Person, mother: Person) {

        if (mother.sex == Person.Sex.FEMALE)
            try {
                if (child.motherId == null) {
                    child.addRelationship(Pair(Person.Relate.Mother, mother))
                    mother.addRelationship(Pair(Person.Relate.Child, child))
                }
            } catch (ex: java.lang.IllegalArgumentException) {
            }
    }

    fun `สร้างความสัมพันธ์พ่อ`(child: Person, father: Person) {
        if (father.sex == Person.Sex.MALE)
            try {
                if (child.fatherId == null) {
                    child.addRelationship(Pair(Person.Relate.Father, father))
                    father.addRelationship(Pair(Person.Relate.Child, child))
                }
            } catch (ex: java.lang.IllegalArgumentException) {
            }
    }
}

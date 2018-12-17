package ffc.airsync.api.genogram

import ffc.airsync.api.person.persons
import ffc.entity.Person

internal fun List<Person>.createMate(person: Person) {
    person.link?.keys?.get("mateid")?.let { personId ->
        persons.find { checkId(it, personId) }?.let {
            `สร้างความสัมพันธ์ภรรยา`(person, it)
        }
    }
    if (!person.haveSpouse())
        person.link?.keys?.get("mate")?.let { mateName ->
            find { checkName(it, mateName) }?.let {
                `สร้างความสัมพันธ์ภรรยา`(person, it)
            }
        }
}

internal fun List<Person>.creteMother(person: Person) {
    person.link?.keys?.get("motherid")?.let { personId ->
        persons.find { checkId(it, personId) }?.let {
            `สร้างความสัมพันธ์แม่`(person, it)
        }
    }

    if (!person.haveMother())
        person.link?.keys?.get("mother")?.let { motherName ->
            find { checkName(it, motherName) }?.let {
                `สร้างความสัมพันธ์แม่`(person, it)
            }
        }
}

internal fun List<Person>.createFather(person: Person) {
    person.link?.keys?.get("fatherid")?.let { personId ->
        persons.find { checkId(it, personId) }?.let {
            `สร้างความสัมพันธ์พ่อ`(person, it)
        }
    }

    if (!person.haveFather())
        person.link?.keys?.get("father")?.let { fatherName ->
            find { checkName(it, fatherName) }?.let {
                `สร้างความสัมพันธ์พ่อ`(person, it)
            }
        }
}

private fun checkId(it: Person, personId: Any) = it.identities.find { it.id == personId } != null
private fun checkName(it: Person, name: Any) = it.name == name as String

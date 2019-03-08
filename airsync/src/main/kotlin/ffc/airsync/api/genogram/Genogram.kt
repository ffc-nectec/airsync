package ffc.airsync.api.genogram

import ffc.airsync.api.person.persons
import ffc.airsync.printDebug
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.Person
import ffc.entity.Person.Relate.Child
import ffc.entity.Person.Relate.Father
import ffc.entity.Person.Relate.Married
import ffc.entity.Person.Relate.Mother
import ffc.entity.Person.Relate.Sibling

fun ArrayList<Person>.initRelation(progressCallback: (Int) -> Unit) {
    val localRelation = arrayListOf<Person>().apply {
        addAll(load("relation.json"))
    }

    if (localRelation.isEmpty()) {
        this.addAll(persons)
        this.forEach {
            it.relationships.clear()
        }
        `สร้างความสัมพันธ์`(progressCallback)
        updateToCloud(progressCallback)
        save("relation.json")
    } else {
        this.addAll(localRelation)
    }
    progressCallback(100)
}

private fun List<Person>.updateToCloud(progressCallback: (Int) -> Unit) {
    val personBlock = hashMapOf<String, List<Person.Relationship>>()

    forEach {
        if (it.relationships.isNotEmpty()) {
            personBlock[it.id] = it.relationships
        }
    }
    geonogramApi.putBlock(personBlock, progressCallback).forEach { personId, relation ->
        this.find { it.id == personId }!!.relationships = relation.toMutableList()
    }
}

private fun List<Person>.`สร้างความสัมพันธ์`(progressCallback: (Int) -> Unit) {
    val houseMap = groupByHcode()
    val groupByFamilyNo = groupFamilyNo(houseMap)

    val size = groupByFamilyNo.count()
    var index = 1
    groupByFamilyNo.forEach { key, house ->
        index++
        if (index % 300 == 0 || index == size)
            printDebug("createRela $index:$size")
        house.forEach { person ->
            val familyPosition = (person.link?.keys?.get("familyposition") ?: "") as String
            if (familyPosition.isNotBlank()) {
                if (!person.haveFather()) {
                    val father = `ค้นหาความสัมพันธ์ในบ้าน`(house, fatherFamilyPosition(familyPosition))
                    if (father.isNotEmpty() && person.fatherId == null) {
                        if (person.sex == Person.Sex.MALE)
                            `สร้างความสัมพันธ์พ่อ`(person, father.first())
                        else
                            `สร้างความสัมพันธ์แม่`(person, father.first())
                    }
                }

                if (!person.haveMother()) {
                    val mother = `ค้นหาความสัมพันธ์ในบ้าน`(house, motherFamilyPosition(familyPosition))
                    if (mother.isNotEmpty() && person.motherId == null) {
                        if (person.sex == Person.Sex.FEMALE)
                            `สร้างความสัมพันธ์แม่`(person, mother.first())
                        else
                            `สร้างความสัมพันธ์พ่อ`(person, mother.first())
                    }
                }

                if (!person.haveSpouse()) {
                    val mate = `ค้นหาความสัมพันธ์ในบ้าน`(house, mateFamilyPosition(familyPosition))
                    if (mate.isNotEmpty())
                        `สร้างความสัมพันธ์ภรรยา`(person, mate.first())
                }

                val child = `ค้นหาความสัมพันธ์ในบ้าน`(house, childPosition(familyPosition, person.haveSpouse()))
                if (child.isNotEmpty())
                    `สร้างความสัมพันธ์ลูก`(person, child)

                val childWithMate = `ค้นหาความสัมพันธ์ในบ้าน`(house, childWithMatePosition(familyPosition))
                if (childWithMate.isNotEmpty())
                    `สร้างความสัมพันธ์ลูก`(person, childWithMate)

                val sibling = `ค้นหาความสัมพันธ์ในบ้าน`(house, siblingPosition(familyPosition))
                if (sibling.isNotEmpty())
                    `สร้างความสัมพันธ์พี่น้อง`(person, sibling)
            }
        }
        progressCallback((index * 50) / size)
    }
}

private fun List<Person>.groupFamilyNo(
    houseMap: HashMap<String, ArrayList<Person>>
): HashMap<String, ArrayList<Person>> {
    val groupByFamilyNo = HashMap<String, ArrayList<Person>>()
    val size = houseMap.count()
    var i = 1
    houseMap.forEach { hcode, houseGroupByHcode ->
        i++
        if (i % 300 == 0 || i == size)
            printDebug("groupFamilyNo $i:$size")
        houseGroupByHcode.forEach { person ->

            `ค้นหาพ่อแม่ภรรยาจากหมายเลขบัตรประชาชนและชื่อ`(person)

            val familyno = (person.link?.keys?.get("familyno") ?: "") as String
            if (groupByFamilyNo["$hcode:$familyno"] == null) groupByFamilyNo["$hcode:$familyno"] = arrayListOf()
            groupByFamilyNo["$hcode:$familyno"]!!.add(person)
        }
    }
    return groupByFamilyNo
}

private fun List<Person>.groupByHcode(): HashMap<String, ArrayList<Person>> {
    val houseMap = HashMap<String, ArrayList<Person>>()
    forEach {
        val hcode = it.link?.keys?.get("hcode") as String
        if (hcode == "1") return@forEach
        if (houseMap[hcode] == null) houseMap[hcode] = arrayListOf()

        houseMap[hcode]!!.add(it)
    }
    return houseMap
}

private fun `ค้นหาความสัมพันธ์ในบ้าน`(
    house: ArrayList<Person>,
    personFamilyPosition: String
): List<Person> {
    return if (personFamilyPosition.isNotBlank())
        house.filter { ((it.link?.keys?.get("familyposition") ?: "") as String) == personFamilyPosition }
    else
        arrayListOf()
}

private fun `ค้นหาความสัมพันธ์ในบ้าน`(
    house: ArrayList<Person>,
    personFamilyPosition: List<String>
): List<Person> {
    return if (personFamilyPosition.isNotEmpty()) {
        val out = ArrayList<Person>()
        personFamilyPosition.forEach {
            out.addAll(`ค้นหาความสัมพันธ์ในบ้าน`(house, it))
        }
        out
    } else
        arrayListOf()
}

private fun List<Person>.`ค้นหาพ่อแม่ภรรยาจากหมายเลขบัตรประชาชนและชื่อ`(person: Person) {
    createFather(person)
    creteMother(person)
    createMate(person)
}

internal fun Person.haveFather(): Boolean = fatherId != null
internal fun Person.haveMother(): Boolean = motherId != null
internal fun Person.haveSpouse(): Boolean = spouseId != null
internal fun Person.haveSibling(): Boolean = siblingId.isNotEmpty()
internal fun Person.haveChild(): Boolean = childId.isNotEmpty()

internal fun `สร้างความสัมพันธ์ภรรยา`(person: Person, it: Person) {
    try {
        person.addRelationship(Pair(Married, it))
        it.addRelationship(Pair(Married, person))
    } catch (ex: java.lang.IllegalArgumentException) {
        // printDebug("Error ความสัมพันธ์ภรรยา")
        // ex.printStackTrace()
    }
}

internal fun `สร้างความสัมพันธ์แม่`(child: Person, mother: Person) {

    try {
        if (child.motherId == null) {
            child.addRelationship(Pair(Mother, mother))
            mother.addRelationship(Pair(Child, child))
        }
    } catch (ex: java.lang.IllegalArgumentException) {
        // printDebug("Error ความสัมพันธ์แม่")
        // ex.printStackTrace()
    }
}

internal fun `สร้างความสัมพันธ์พ่อ`(child: Person, father: Person) {
    try {
        if (child.fatherId == null) {
            child.addRelationship(Pair(Father, father))
            father.addRelationship(Pair(Child, child))
        }
    } catch (ex: java.lang.IllegalArgumentException) {
        // printDebug("Error ความสัมพันธ์พ่อ")
        // ex.printStackTrace()
    }
}

internal fun `สร้างความสัมพันธ์ลูก`(head: Person, child: List<Person>) {
    val relationToHead = if (head.sex == Person.Sex.FEMALE) Mother else Father
    try {

        child.forEach {
            if (relationToHead == Mother && it.motherId == null) {
                head.addRelationship(Pair(Child, it))
                it.addRelationship(Pair(relationToHead, head))
            } else if (relationToHead == Father && it.fatherId == null) {
                head.addRelationship(Pair(Child, it))
                it.addRelationship(Pair(relationToHead, head))
            }
        }
    } catch (ex: java.lang.IllegalArgumentException) {
        // printDebug("Error ความสัมพันธ์ลูก")
        // ex.printStackTrace()
    }
}

internal fun `สร้างความสัมพันธ์พี่น้อง`(head: Person, child: List<Person>) {
    try {

        child.forEach {
            head.addRelationship(Pair(Child, it))
            it.addRelationship(Pair(Sibling, head))
        }
    } catch (ex: java.lang.IllegalArgumentException) {
        // printDebug("Error ความสัมพันธ์พี่น้อง")
        // ex.printStackTrace()
    }
}

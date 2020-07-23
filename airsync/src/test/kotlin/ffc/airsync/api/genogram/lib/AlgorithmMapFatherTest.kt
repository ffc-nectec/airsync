package ffc.airsync.api.genogram.lib

import ffc.airsync.api.genogram.lib.GENOSEX.FEMALE
import ffc.airsync.api.genogram.lib.GENOSEX.MALE
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.junit.Test

class AlgorithmMapFatherTest {

    private data class FatherTest(val ownIdCard: String, val sex: GENOSEX?, val fatherInformationIdCard: String?) {
        var fatherRelation: String? = null
    }

    @Test
    fun mapFatherById() {
        val alg = AlgorithmMapFather<FatherTest>()
        val persons = arrayListOf(
            Person("1", "1", FatherTest("1", FEMALE, "2"), "สม"),
            Person("1", "1", FatherTest("2", MALE, "1"), "หมาย"),
            Person("1", "1", FatherTest("3", MALE, "2"), "ใจ"),
            Person("1", "1", FatherTest("4", MALE, null), "มั่น"),
            Person("1", "1", FatherTest("5", MALE, ""), "คง")
        )

        val func: (person: FatherTest) -> AlgorithmMapFather.MapFatherByIdGetData<FatherTest> = { person ->
            object : AlgorithmMapFather.MapFatherByIdGetData<FatherTest> {
                override val idCard: String = person.ownIdCard
                override val sex: GENOSEX? = person.sex
                override val fatherInformationIdCard: String? = person.fatherInformationIdCard
                override val fatherInRelation: FatherTest?
                    get() {
                        val fatherRelation = person.fatherRelation ?: return null
                        return persons.find {
                            fatherRelation == it.person.ownIdCard
                        }?.person
                    }

                override fun setFather(fatherIdCard: String) {
                    person.fatherRelation = fatherIdCard
                }
            }
        }

        alg.mapFatherById(persons, func)

        persons.find { it.name == "สม" }!!.person.fatherRelation!! `should be equal to` "2"
        persons.find { it.name == "ใจ" }!!.person.fatherRelation!! `should be equal to` "2"
        persons.find { it.name == "หมาย" }!!.person.fatherRelation `should be` null
        persons.find { it.name == "มั่น" }!!.person.fatherRelation `should be` null
        persons.find { it.name == "คง" }!!.person.fatherRelation `should be` null
    }
}

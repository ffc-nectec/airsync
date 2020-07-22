package ffc.airsync.api.genogram.lib

import org.amshove.kluent.`should be equal to`
import org.junit.Test

class GenogramUtilTest {
    private val person1_1 = Person("123", "1", "1")
    private val person1_2 = Person("123", "1", "2")
    private val person2_1 = Person("123", "2", "3")
    private val person3_1 = Person("321", "2", "3")

    private val persons = listOf(person1_1, person1_2, person2_1, person3_1)

    @Test
    fun personInHouseTest() {
        val util = GenogramUtil<String>()
        val testInHouse = util.personGroupHouse(persons)

        testInHouse.size `should be equal to` 3
        testInHouse.get("123" to "1")!!.size `should be equal to` 2
        testInHouse.get("123" to "2")!!.size `should be equal to` 1
    }
}

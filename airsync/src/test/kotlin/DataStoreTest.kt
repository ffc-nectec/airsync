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

import ffc.airsync.utils.cleanFile
import ffc.airsync.utils.getClassNameInList
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.Link
import ffc.entity.Person
import ffc.entity.System
import ffc.entity.ThaiCitizenId
import ffc.entity.ThaiHouseholdId
import ffc.entity.place.House
import ffc.entity.update
import me.piruin.geok.geometry.Point
import org.amshove.kluent.`should be equal to`
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.junit.After
import org.junit.Before
import org.junit.Test

class DataStoreTest {

    val person = Person("e079e175c75a44f180e8eaeb").update(DateTime.parse("2018-06-25T14:09:07.815+07:00")) {
        identities.add(ThaiCitizenId("1154801544875"))
        prename = "นาย"
        firstname = "พิรุณ"
        lastname = "พานิชผล"
        birthDate = LocalDate.parse("1993-06-29")
        link = Link(
            System.JHICS, "pid" to "1234567", "cid" to "11014578451234",
            lastSync = DateTime.parse("2018-06-25T14:09:07.815+07:00")
        )
        bundle["orgId"] = "00108"
        bundle["houseId"] = "12321237896"
    }
    val house = House("123f678f90c").update(DateTime.parse("2018-06-25T14:09:07.815+07:00")) {
        identity = ThaiHouseholdId("10125501411")
        link = Link(System.JHICS, "hid" to "100234", lastSync = timestamp)
        no = "302/21"
        road = "รังสิต-นครนายก"
        location = Point(14.077196, 100.5995609)
    }

    @Before
    fun setUp() {
        arrayListOf<Person>().cleanFile()
    }

    @After
    fun tearDown() {
        arrayListOf<Person>().cleanFile()
    }

    @Test
    fun getClassNameInList() {
        val persons = arrayListOf<Person>().apply {
            add(person)
        }

        getClassNameInList(persons) `should be equal to` "Person"
    }

    @Test
    fun clearFile() {
        arrayListOf<Person>().apply {
            add(person)
        }.save()

        arrayListOf<Person>().cleanFile()
    }

    @Test
    fun loadEmptyFile() {
        val person = arrayListOf<Person>().load()
        person.isEmpty() `should be equal to` true
    }

    @Test
    fun saveAndLoadPerson() {
        arrayListOf<Person>().apply {
            add(person)
        }.save()

        arrayListOf<House>().apply {
            add(house)
            add(house)
        }.save()

        val persons = arrayListOf<Person>().load()
        val house = arrayListOf<House>().load()

        persons.count() `should be equal to` 1
        persons.last().id `should be equal to` "e079e175c75a44f180e8eaeb"

        house.first().link!!.keys["hid"] as String `should be equal to` "100234"
    }
}

/*
 * Copyright (c) 2561 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.airsync.api.dao

import ffc.model.People
import ffc.model.Person
import ffc.model.StorageOrg
import ffc.model.printDebug
import java.util.*
import javax.ws.rs.NotFoundException
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class InMemoryPersonDao : PersonDao {

    private constructor()

    companion object {
        val instant = InMemoryPersonDao()
    }


    val personList: ArrayList<StorageOrg<Person>> = arrayListOf()


    val peopleList = arrayListOf<StorageOrg<HashMap<Int, ArrayList<People>>>>() //คนในบ้าน


    override fun removeByOrgUuid(orgUUID: UUID) {
        peopleList.removeIf {
            it.uuid==orgUUID
        }
        personList.removeIf {
            it.uuid==orgUUID
        }


    }



    override fun insert(orgUUID: UUID, person: Person) {
        personList.removeIf { it.uuid == orgUUID && it.data.pid == person.pid }
        personList.add(StorageOrg(orgUUID, person))
        peopleToHouse(orgUUID = orgUUID, person = person)
    }

    override fun insert(orgUUID: UUID, personList: List<Person>) {
        personList.forEach {
            insert(orgUUID, it)
        }
    }

    override fun find(orgUuid: UUID): List<StorageOrg<Person>> {
        val data = personList.filter { it.uuid == orgUuid }
        if (data.size < 1) throw NotFoundException()
        return data
    }

    override fun remove(orgUuid: UUID) {
        personList.removeIf { it.uuid == orgUuid }
    }

    private fun peopleToHouse(orgUUID: UUID, person: Person) {

        val houseId = person.houseId
        if (houseId == null) {
            return
        }

        var peopleInOrg = getPeopleInOrg(orgUUID)
        if (peopleInOrg == null) {
            peopleInOrg = HashMap()
            peopleList.add(StorageOrg(orgUUID, peopleInOrg))
        }


        var peopleInHouse = peopleInOrg.get(houseId)
        if (peopleInHouse == null) {
            peopleInHouse = arrayListOf<People>()
            peopleInOrg.put(houseId, peopleInHouse)
        }


        val name = person.prename + " " + person.firstname + " " + person.lastname
        val cardId = person.identities[0].id
        val people = People(cardId, name)


        peopleInHouse.removeIf { it.id == cardId }
        peopleInHouse.add(people)


    }

    override fun getPeopleInHouse(orgUUID: UUID, houseId: Int): ArrayList<People>? {
        val peopleInOrg = getPeopleInOrg(orgUUID = orgUUID)



        if (peopleInOrg == null) {
            printDebug("People in org Null")
            return null
        }


        val peopleInHouse = peopleInOrg[houseId]


        if (peopleInHouse == null) {
            printDebug("House find null $houseId")
        }

        printDebug("Print all people in house $houseId")
        peopleInHouse?.forEach { printDebug(it) }

        return peopleInHouse
    }

    private inline fun getPeopleInOrg(orgUUID: UUID): HashMap<Int, ArrayList<People>>? {
        val peopleInOrg = peopleList.find { it.uuid == orgUUID }?.data //int เป็น id บ้าน
        return peopleInOrg

    }


}

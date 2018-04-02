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

import ffc.model.PersonOrg
import java.util.*
import kotlin.collections.ArrayList

class InMemoryPersonDao : PersonDao {

    private constructor()

    companion object {
        val instant = InMemoryPersonDao()
    }

    val personList: ArrayList<PersonOrg> = arrayListOf()


    override fun insert(orgUUID: UUID, person: PersonOrg) {
        personList.removeIf { it.citizenId == person.citizenId }
        person.orgUUID = orgUUID
        personList.add(person)
    }

    override fun insert(orgUUID: UUID, personList: List<PersonOrg>) {
        personList.forEach {
            insert(orgUUID, it)
        }
    }

    override fun find(orgUuid: UUID): List<PersonOrg> {
        return personList.filter { it.orgUUID == orgUuid }
    }

    override fun findByCitizen(citizenId: String): List<PersonOrg> {
        return personList.filter { it.citizenId == citizenId }
    }

    override fun remove(orgUuid: UUID) {
        personList.removeIf { it.orgUUID == orgUuid }
    }
}

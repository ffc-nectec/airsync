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

import ffc.model.Person
import ffc.model.StorageOrg
import java.util.*
import kotlin.collections.ArrayList

class InMemoryPersonDao : PersonDao {

    private constructor()

    companion object {
        val instant = InMemoryPersonDao()
    }

    val personList: ArrayList<StorageOrg<Person>> = arrayListOf()


    override fun insert(orgUUID: UUID, person: Person) {
        personList.removeIf { it.uuid == orgUUID && it.data.pid == person.pid }

        personList.add(StorageOrg(orgUUID, person))
    }

    override fun insert(orgUUID: UUID, personList: List<Person>) {
        personList.forEach {
            insert(orgUUID, it)
        }
    }

    override fun find(orgUuid: UUID): List<StorageOrg<Person>> {
        return personList.filter { it.uuid == orgUuid }
    }

    override fun remove(orgUuid: UUID) {
        personList.removeIf { it.uuid == orgUuid }
    }
}

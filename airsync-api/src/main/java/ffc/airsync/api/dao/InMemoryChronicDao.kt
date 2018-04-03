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

import ffc.model.Chronic
import ffc.model.StorageOrg
import java.util.*

class InMemoryChronicDao : ChronicDao {

    private constructor()

    companion object {
        val instant = InMemoryChronicDao()
    }

    val chronicList = arrayListOf<StorageOrg<Chronic>>()

    override fun insert(orgUUID: UUID, chronic: Chronic) {
        chronicList.add(StorageOrg(orgUUID, chronic))
    }

    override fun insert(orgUUID: UUID, chronicList: List<Chronic>) {
        chronicList.forEach {
            insert(orgUUID, it)
        }
    }

    override fun find(orgUuid: UUID): List<StorageOrg<Chronic>> {
        return chronicList.filter { it.uuid == orgUuid }
    }

    override fun remove(orgUuid: UUID) {
        chronicList.removeIf { it.uuid == orgUuid }
    }
}

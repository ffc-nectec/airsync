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

package ffc.airsync.api.genogram

import ffc.airsync.api.genogram.lib.GenogramProcessWatcarakorn
import ffc.airsync.geonogramApi
import ffc.airsync.utils.getLogger
import ffc.entity.Person
import ffc.entity.Person.Relationship

class SyncGenogram {
    private val logger = getLogger(this)

    interface Func {
        val person: List<Person>
        fun updatePerson(objectId: String, relation: List<Relationship>)
    }

    fun sync(func: () -> Func) {
        logger.info { "Genogram process..." }
        val personTemp = arrayListOf<Person>()
        personTemp.addAll(func().person)
        personTemp.forEach {
            it.relationships.clear()
        }
        GenogramProcessWatcarakorn(FFCAdapterPersonDetailInterface(personTemp)).process(personTemp)
        personTemp.updateToCloud(func)
    }

    private fun List<Person>.updateToCloud(func: () -> Func) {
        val personBlock = hashMapOf<String, List<Relationship>>()

        forEach {
            if (it.relationships.isNotEmpty()) {
                personBlock[it.id] = it.relationships
            }
        }
        geonogramApi.putBlock(personBlock) {}.forEach { (personId, relation) ->
            this.find { it.id == personId }?.relationships = relation.toMutableList()
            func().updatePerson(personId, relation)
        }
    }
}

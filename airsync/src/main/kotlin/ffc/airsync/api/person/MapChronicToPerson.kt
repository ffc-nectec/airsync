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

package ffc.airsync.api.person

import ffc.entity.Link
import ffc.entity.Person
import ffc.entity.healthcare.Chronic

/**
 * นำคนมาแมพหา chronic
 */
class MapChronicToPerson(persons: List<Person>, chronics: List<Chronic>) {
    private val cachePerson = persons.mapNotNull { person ->
        val pid = person.link?.keys?.get("pid")?.toString()
        if (pid != null) {
            pid to person
        } else
            null
    }.toMap().toSortedMap()

    private val chronic = chronics.mapNotNull { chronic ->
        val pid = chronic.link?.keys?.get("pid")?.toString()
        if (pid != null) {
            pid to chronic
        } else
            null
    }

    fun run() {
        chronic.forEach { chronicItem ->
            cachePerson[chronicItem.first]?.addChronic(chronicItem.second)
        }
    }

    private fun Person.addChronic(chronic: Chronic) {
        if (!chronics.contain(chronic)) {
            chronics.add(chronic)
        }
    }

    private fun List<Chronic>.contain(other: Chronic): Boolean {
        forEach { chronic ->
            if (chronic.equal(other)) return true
        }
        return false
    }

    private fun Chronic.equal(other: Chronic?): Boolean {
        if (this === other) return true
        if (other !is Chronic) return false
        if (diagDate != other.diagDate) return false
        if (dischardDate != other.dischardDate) return false
        if (disease != other.disease) return false
        if (link != null)
            if (link!!.equal(other.link)) return true
        return true
    }

    private fun Link.equal(other: Link?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (isSynced != other.isSynced) return false
        if (system != other.system) return false
        keys.forEach { (t, u) ->
            if (u is String) {
                if (other.keys[t].toString() != u.toString()) return false
            } else {
                if (other.keys[t] == null) return false
            }
        }
        return true
    }
}

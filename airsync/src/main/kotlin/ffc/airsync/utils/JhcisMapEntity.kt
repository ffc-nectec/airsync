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

package ffc.airsync.utils

import ffc.entity.Entity
import ffc.entity.Link

data class JhcisMapEntity(
    val id: String,
    val type: String,
    val link: Link?
) {
    override fun equals(other: Any?): Boolean {

        if (other == this) return true
        if (other is Entity) {
            return (other.id == id && other.type == type)
        }

        if (other is Link) {
            if (link == null) return false
            if (other.system == link.system) {
                val otherKey = other.keys.toList()
                for (i in 0 until otherKey.size) {
                    if (link.keys[otherKey[i].first] != otherKey[i].second)
                        return false
                }
                return true
            }
        }
        return false
    }
}

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

package ffc.airsync.api.user.sync

import ffc.airsync.api.user.sync.UserDataStatus.CREATE
import ffc.airsync.api.user.sync.UserDataStatus.EQUAL
import ffc.airsync.api.user.sync.UserDataStatus.UPDATE
import ffc.entity.User

/**
 * จับคู่ข้อมูล One กับ Two
 * พร้อมทั้ง check timestamp update
 */
internal fun mapUserOneWithTwo(one: List<User>, two: List<User>): List<Triple<User, User?, UserDataStatus>> {
    val result = arrayListOf<Triple<User, User?, UserDataStatus>>()
    one.forEach { localItem ->
        val find = two.find { cloudItem -> localItem.eq(cloudItem) }
        if (find == null)
            result.add(Triple(localItem, null, CREATE))
        else {
            result.add(Triple(localItem, find, if (localItem.timestamp > find.timestamp) UPDATE else EQUAL))
        }
    }

    return result.toList()
}

enum class UserDataStatus {
    CREATE, UPDATE, EQUAL
}

internal fun User.eq(eq: User): Boolean {
    if (this.name == eq.name) return true
    val thisIdCard = this.getIdCard()
    val eqIdCard = eq.getIdCard()
    if (thisIdCard.isNullOrBlank()) return false
    if (eqIdCard.isNullOrBlank()) return false
    if (thisIdCard == eqIdCard) return true
    return false
}

internal fun User.getIdCard(): String? {
    return this.link?.keys?.get("idcard")?.toString()
}

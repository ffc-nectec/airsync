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

package ffc.airsync

import ffc.airsync.api.person.findPersonId
import org.apache.logging.log4j.kotlin.logger

val lookupPersonId = { pid: String ->
    try {
        findPersonId(pid)
    } catch (ex: KotlinNullPointerException) {
        logger("Lookup").warn(ex) { "Lookup person Error ${ex.message}" }
        ""
    }
}
val lookupUserId = { name: String ->
    try {
        findProviderId(name)
    } catch (ex: KotlinNullPointerException) {
        logger("Lookup").warn(ex) { "Lookup user Error ${ex.message}" }
        ""
    }
}
val lookupDisease = { icd10: String -> icd10Api.lookup(icd10) }
val lookupServiceType = { serviceId: String -> homeHealthTypeApi.lookup(serviceId) }
val lookupSpecialPP = { ppCode: String -> specialPpApi.lookup(ppCode.trim()) }

private fun findProviderId(name: String): String {
    val id = (userManage.cloudUser.find { it.name == name })?.id
    return if (id == null) {
        userManage.sync()
        (userManage.cloudUser.find { it.name == name })?.id ?: ""
    } else
        id
}

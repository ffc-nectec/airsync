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

package ffc.airsync.api.healthcare

import ffc.airsync.Main
import ffc.airsync.lookupDisease
import ffc.airsync.lookupPersonId
import ffc.airsync.lookupServiceType
import ffc.airsync.lookupSpecialPP
import ffc.airsync.lookupUserId
import ffc.airsync.utils.load
import ffc.entity.healthcare.HealthCareService

fun ArrayList<HealthCareService>.initSync(progressCallback: (Int) -> Unit) {
    val cacheFile = arrayListOf<HealthCareService>().apply {
        addAll(load())
    }

    val sync = SyncHealthCare(Main.instant.dao)
    if (cacheFile.isEmpty()) {
        val jhcisVisit = getHealthCareFromDb(progressCallback)
        sync.localToCloud(cacheFile, jhcisVisit, this, progressCallback)
    } else {
        addAll(cacheFile)
    }
    progressCallback(100)
}

private fun getHealthCareFromDb(progressCallback: (Int) -> Unit): List<HealthCareService> {
    /*return Main.instant.dao.getHealthCareService(
        lookupPatientId = { pid -> persons.find { it.link!!.keys["pid"] == pid }?.id ?: "" },
        lookupProviderId = { name -> (users.find { it.name == name } ?: users.last()).id }
    )*/

    return Main.instant.dao.getHealthCareService(
        lookupPatientId = lookupPersonId,
        lookupProviderId = lookupUserId,
        lookupDisease = lookupDisease,
        lookupServiceType = lookupServiceType,
        lookupSpecialPP = lookupSpecialPP,
        progressCallback = progressCallback
    )
}

private const val healthCareLock = "lock"

fun List<HealthCareService>.lock(f: () -> Unit) {
    synchronized(healthCareLock) {
        f()
    }
}

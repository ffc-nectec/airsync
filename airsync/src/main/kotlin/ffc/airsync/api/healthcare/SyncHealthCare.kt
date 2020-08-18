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

import ffc.airsync.api.Sync
import ffc.airsync.db.DatabaseDao
import ffc.airsync.healthCareApi
import ffc.airsync.utils.checkNewDataCreate
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.airsync.utils.syncCloud
import ffc.entity.Entity
import ffc.entity.gson.toJson
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit

class SyncHealthCare(private val dao: DatabaseDao) : Sync {
    override fun sync(force: Boolean): List<Entity>? {
        cloudToLocal()
        return null
    }

    private fun cloudToLocal() {
        syncCloud.syncFilter(
            dao, listOf(
                HealthCareService::class.java.simpleName,
                HomeVisit::class.java.simpleName
            )
        )
    }

    fun localToCloud(
        cacheFile: ArrayList<HealthCareService>,
        jhcisVisit: List<HealthCareService>,
        healthCareList: ArrayList<HealthCareService>,
        progressCallback: (Int) -> Unit
    ) {
        hashMapOf<String, Long>("maxvisit" to dao.queryMaxVisit()).save("maxvisit.json")
        val temp = listOf<HealthCareService>().load("healthTemp.json")
        if (temp.isEmpty()) {
            cacheFile.addAll(jhcisVisit)
            cacheFile.save("healthTemp.json")
        } else
            cacheFile.addAll(temp)
        healthCareList.addAll(healthCareApi.clearAndCreateHealthCare(cacheFile, progressCallback))
        healthCareList.save()
    }

    fun syncNewHealthCareLocalToCloud(
        jhcisVisit: List<HealthCareService>,
        cacheFile: ArrayList<HealthCareService>,
        healthCareList: ArrayList<HealthCareService>,
        progressCallback: (Int) -> Unit
    ) {
        checkNewDataCreate(jhcisVisit, cacheFile, { jhcis, cloud ->
            val pcuCheck = runCatching { jhcis.link!!.keys["pcucode"] == cloud.link!!.keys["pcucode"] }
            val visitNoCheck = runCatching { jhcis.link!!.keys["visitno"] == cloud.link!!.keys["visitno"] }

            if (pcuCheck.isSuccess && visitNoCheck.isSuccess) {
                pcuCheck.getOrThrow() && visitNoCheck.getOrThrow()
            } else false
        }) {
            getLogger(healthCareList).info { "Create new visit ${it.toJson()}" }
            healthCareList.addAll(healthCareApi.clearAndCreateHealthCare(it, progressCallback, false))
            healthCareList.save()
        }
    }
}

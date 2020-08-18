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

import ffc.airsync.db.DatabaseDao
import ffc.entity.healthcare.HealthCareService

interface HealthCareApi {
    fun syncHealthCareFromCloud(id: String, dao: DatabaseDao)
    fun clearAndCreateHealthCare(
        healthCare: List<HealthCareService>,
        progressCallback: (Int) -> Unit,
        clearCloud: Boolean = true
    ): List<HealthCareService>

    fun createHealthCare(
        healthCare: List<HealthCareService>,
        progressCallback: (Int) -> Unit
    ): List<HealthCareService>

    fun updateHealthCare(healthCareService: HealthCareService): HealthCareService
}

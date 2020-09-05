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

package ffc.airsync.visit

import ffc.entity.Person
import ffc.entity.healthcare.CommunityService
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit
import ffc.entity.healthcare.Icd10
import ffc.entity.healthcare.SpecialPP

interface VisitDao {
    fun createHomeVisit(
        homeVisit: HomeVisit,
        healthCareService: HealthCareService,
        pcucode: String,
        pcucodePerson: String,
        patient: Person,
        username: String
    ): HealthCareService

    fun updateHomeVisit(
        homeVisit: HomeVisit,
        healthCareService: HealthCareService,
        pcucode: String,
        pcucodePerson: String,
        patient: Person,
        username: String
    ): HealthCareService

    fun getHealthCareService(
        lookupPatientId: (pcuCode: String, pid: String) -> String,
        lookupProviderId: (name: String) -> String,
        lookupDisease: (icd10: String) -> Icd10?,
        lookupSpecialPP: (ppCode: String) -> SpecialPP.PPType?,
        lookupServiceType: (serviceId: String) -> CommunityService.ServiceType?,
        whereString: String = "",
        progressCallback: (Int) -> Unit = {}
    ): List<HealthCareService>

    fun getMaxVisit(): Long
}

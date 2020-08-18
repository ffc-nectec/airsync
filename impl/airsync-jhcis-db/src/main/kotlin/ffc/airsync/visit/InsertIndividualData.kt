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

import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit
import org.joda.time.DateTime
import java.sql.Timestamp

class InsertIndividualData(
    homeVisit: HomeVisit,
    healthCareService: HealthCareService,
    val pcucode: String,
    val visitno: Long,
    val username: String
) {
    val patientsign = healthCareService.syntom
    val homehealthdetail = homeVisit.detail
    val homehealthresult = homeVisit.result
    val homehealthplan = homeVisit.plan
    val dateappoint =
        if (healthCareService.nextAppoint != null)
            Timestamp(healthCareService.nextAppoint!!.toDate().time)
        else
            null

    val user = username
    val dateupdate = Timestamp(DateTime.now().plusHours(7).millis)

    val homehealthtype = homeVisit.serviceType.id
}

fun HomeVisit.buildInsertIndividualData(
    healthCareService: HealthCareService,
    pcucode: String,
    visitno: Long,
    username: String
): InsertIndividualData {
    return InsertIndividualData(this, healthCareService, pcucode, visitno, username)
}

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

import ffc.entity.healthcare.Diagnosis
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.Icd10
import org.joda.time.DateTime
import java.sql.Timestamp

class InsertDiagData(
    val healthCareService: HealthCareService,
    val pcucode: String,
    val visitno: Long,
    val username: String
) {
    lateinit var diagcode: String
    lateinit var conti: String
    lateinit var dxtype: String
    val dateupdate: Timestamp = Timestamp(DateTime.now().plusHours(7).millis)
    val doctordiag = username
    val appointdate =
        if (healthCareService.nextAppoint != null)
            Timestamp(healthCareService.nextAppoint!!.toDate().time)
        else
            null
}

fun HealthCareService.buildInsertDiag(
    pcucode: String,
    visitno: Long,
    username: String
): Iterable<InsertDiagData> {
    return this.diagnosises.map {
        InsertDiagData(this, pcucode, visitno, username).apply {
            if (it.disease is Icd10)
                diagcode = (it.disease as Icd10).icd10.trim()
            conti = if (it.isContinued) "1" else "0"
            dxtype = when (it.dxType) {
                Diagnosis.Type.PRINCIPLE_DX -> "01"
                Diagnosis.Type.CO_MORBIDITY -> "02"
                Diagnosis.Type.COMPLICATION -> "03"
                Diagnosis.Type.OTHER -> "04"
                else -> "05"
            }.trim()
        }
    }
}

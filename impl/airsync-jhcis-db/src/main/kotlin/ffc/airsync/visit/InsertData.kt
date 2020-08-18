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
import ffc.entity.healthcare.bloodPressureLevel
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.sql.Time
import java.sql.Timestamp

class InsertData(
    val healthCareService: HealthCareService,
    val pcucode: String,
    val visitno: Long,
    val pcucodeperson: String,
    val pid: Long,
    val username: String,
    val rightcode: String?,
    val rightno: String?,
    val hosmain: String?,
    val hossub: String?

) {
    var vitalcheck: String = ""

    init {
        healthCareService.communityServices.forEach {
            if (it is HomeVisit)
                it.result?.let { result ->
                    if (result.isNotEmpty())
                        vitalcheck = result
                }
        }
    }

    val dateupdate: Timestamp = Timestamp(DateTime.now().plusHours(7).millis)
    val visitdate: Timestamp = Timestamp(healthCareService.time.plusHours(7).millis)

    val timestart: Time = Time(healthCareService.time.plusHours(7).millis)
    val timeend: Time = Time(healthCareService.time.plusHours(7).plusMinutes(5).millis)

    // TODO // error
    val flagservice = "03"

    val symptoms = healthCareService.syntom

    val weight = healthCareService.weight
    val height = healthCareService.height
    val waist = healthCareService.waist
    val ass = healthCareService.ass

    val bmilevel = when {
        healthCareService.bmi == null -> null
        healthCareService.bmi!!.isOverweight -> "5"
        healthCareService.bmi!!.isObese -> "5"
        healthCareService.bmi!!.isNormal -> "3"
        else -> "1"
    }

    val bpLevel = healthCareService.bloodPressureLevel
    val bp = healthCareService.bloodPressure

    val pressure = if (bp != null) "${bp.systolic.toInt()}/${bp.diastolic.toInt()}" else null
    val pressurelevel = when {
        bpLevel == null -> null
        bpLevel.isHigh -> "3"
        bpLevel.isPreHigh -> "2"
        else -> "1"
    }
    val temperature = healthCareService.bodyTemperature
    val flag18fileexpo = "2"
    val pulse = healthCareService.pulseRate
    val respri = healthCareService.respiratoryRate

    val timeservice: Int
        get() {
            return getTimeService(healthCareService.time.withZone(DateTimeZone.forOffsetHours(7)).hourOfDay)
        }

    fun getTimeService(houseOfDay: Int): Int {
        return if (houseOfDay in 9..15) 1 else 2
    }
}

fun HealthCareService.buildInsertData(
    pcucode: String,
    visitno: Long,
    pcucodeperson: String,
    pid: Long,
    username: String,
    rightcode: String?,
    rightno: String?,
    hosmain: String?,
    hossub: String?
): InsertData {
    return InsertData(
        this,
        pcucode,
        visitno,
        pcucodeperson,
        pid,
        username,
        rightcode,
        rightno,
        hosmain,
        hossub
    )
}

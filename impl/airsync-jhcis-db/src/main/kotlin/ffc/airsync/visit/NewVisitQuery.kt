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

import ffc.airsync.Dao
import ffc.airsync.MySqlJdbi
import ffc.airsync.getLogger
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.healthcare.BloodPressure
import ffc.entity.healthcare.HealthCareService
import ffc.entity.update
import ffc.entity.util.generateTempId
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime

internal class NewVisitQuery(val jdbiDao: Dao = MySqlJdbi(null)) {

    private val logger = getLogger(this)

    interface Lookup {
        fun patientId(pcuCode: String, pid: String): String?
        fun providerId(username: String): String?
    }

    fun get(where: String, lookup: () -> Lookup): List<HealthCareService> {

        val sql = if (where.isBlank())
            visitQuery
        else
            visitQuery + """
    AND $where
"""

        return jdbiDao.instant.withHandle<List<HealthCareService>, Exception> { handle ->
            handle.createQuery(sql)
                .map { rs, _ ->
                    val username = rs.getString("username") ?: ""
                    val pid = rs.getString("pid")!!
                    val timestamp = DateTime(rs.getTimestamp("dateupdate")).minusHours(7)
                    val pcuCode = rs.getString("pcucode")!!

                    HealthCareService(
                        providerId = (if (username.isNotEmpty()) lookup().providerId(username) else "") ?: username,
                        patientId = lookup().patientId(pcuCode, pid) ?: "",
                        id = generateTempId()
                    ).update(timestamp) {

                        runBlocking {
                            if (providerId.isBlank() || providerId == username) logger.warn {
                                "Cannot find user $username will set value is $providerId"
                            }
                            if (patientId.isBlank()) logger.warn { "Cannot find person pid $pid" }
                        }
                        rs.getString("symptoms")?.let { syntom = it }

                        val visitdate = rs.getDate("visitdate")

                        rs.getTime("timestart").let { timestart ->
                            if (timestart != null)
                                time = DateTime(visitdate).plus(timestart.time).minusHours(7)
                            else {
                                logger.debug("Visit timestart is null visitno:${rs.getString("visitno")}")
                            }

                            rs.getTime("timeend")?.let { timeend ->
                                try {
                                    endTime = DateTime(visitdate).plus(timeend.time).minusHours(7)
                                } catch (ex: java.lang.IllegalArgumentException) {
                                    logger.warn(
                                        "Visit time end error ตรวจพบข้อมูลขัดแย้งในเรื่องเวลาการ visit " +
                                                "timestart=$time " +
                                                "endtime=${
                                                    DateTime(visitdate).plus(timeend.time)
                                                        .minusHours(7)
                                                } ${ex.message}"
                                    )
                                }
                            }
                        }

                        rs.getString("suggest")?.let { suggestion = it }
                        rs.getString("weight")?.let { weight = it.toDouble() }
                        rs.getString("height")?.let { height = it.toDouble() }
                        rs.getString("waist")?.let { waist = it.toDouble() }
                        rs.getString("ass")?.let { ass = it.toDouble() }
                        rs.getString("respri")?.let { respiratoryRate = it.toDouble() }

                        rs.getString("pressure")?.let {
                            bloodPressure = it.getBloodPressure()
                        }

                        rs.getString("pulse")?.let {
                            pulseRate = it.toDouble()
                        }

                        rs.getString("temperature")?.let { bodyTemperature = it.toDouble() }

                        rs.getString("diagnote")?.let { note = it }

                        link = Link(System.JHICS)
                        link!!.isSynced = true
                        pcuCode.let { link!!.keys["pcucode"] = it }
                        rs.getString("visitno")?.let { link!!.keys["visitno"] = it }
                        pid.let { link!!.keys["pid"] = it }
                        rs.getString("rightcode")?.let { link!!.keys["rightcode"] = it }
                        rs.getString("rightno")?.let { link!!.keys["rightno"] = it }
                        rs.getString("hosmain")?.let { link!!.keys["hosmain"] = it }
                        rs.getString("hossub")?.let { link!!.keys["hossub"] = it }
                    }
                }.list()
        }
    }

    private fun String.getBloodPressure(): BloodPressure? = this.getSystolic()?.let { systolic ->
        this.getDiastolic()?.let { diastolic ->
            BloodPressure(systolic, diastolic)
        }
    }

    private fun String.getSystolic(): Double? =
        Regex("""(\d+)/\d+""").matchEntire(this)?.groupValues?.last()?.toDouble()

    private fun String.getDiastolic(): Double? =
        Regex("""\d+/(\d+)""").matchEntire(this)?.groupValues?.last()?.toDouble()

    val visitQuery = """
SELECT
	visit.pcucode,
	visit.visitno,
	visit.visitdate,
	visit.pcucodeperson,
	visit.pid,
	visit.timeservice,
	visit.timestart,
	visit.timeend,
	visit.symptoms,
	visit.vitalcheck,
	visit.weight,
	visit.height,
	visit.pressure,
	visit.pressure2,
	visit.pressurelevel,
	visit.temperature,
	visit.pulse,
	visit.respri,
	visit.username,
	visit.flagservice,
	visit.dateupdate,
	visit.bmilevel,
	visit.flag18fileexpo,
	visit.rightcode,
	visit.rightno,
	visit.hosmain,
	visit.hossub,
	visit.waist,
	visit.ass,
	visit.healthsuggest1 as suggest,
	visit.diagnote
FROM
	visit
JOIN person ON
	person.pcucodeperson = visit.pcucode AND
	person.pid = visit.pid

    WHERE visit.dateupdate >= NOW() - INTERVAL 3 YEAR
        AND
    visit.timestart IS NOT NULL
		AND
	visit.timeend IS NOT NULL
        AND
    visit.pid <> 0
        	AND
   person.hcode <> 1
"""
}

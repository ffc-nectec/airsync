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

    // TODO // error
    val flagservice = "03"
    val dateupdate: Timestamp = Timestamp(DateTime.now().plusHours(7).millis)

    val visitdate: Timestamp = Timestamp(healthCareService.time.plusHours(7).millis)
    val timestart: Time = Time(healthCareService.time.plusHours(7).millis)
    val timeend: Time = Time(healthCareService.time.plusHours(7).plusMinutes(5).millis)
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

    val bpLevel2 = healthCareService.bloodPressureLevel
    val bp2 = healthCareService.bloodPressure2nd
    val pressure2 = if (bp2 != null) "${bp2.systolic.toInt()}/${bp2.diastolic.toInt()}" else null
    val pressurelevel2 = when {
        bpLevel2 == null -> null
        bpLevel2.isHigh -> "3"
        bpLevel2.isPreHigh -> "2"
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

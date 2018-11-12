package ffc.airsync.db.visit

import ffc.entity.healthcare.HomeVisit
import ffc.entity.healthcare.bloodPressureLevel
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.sql.Time
import java.sql.Timestamp

class InsertData(
    val homeVisit: HomeVisit,
    val pcucode: String,
    val visitno: Long,
    val pcucodeperson: String,
    val pid: Long,
    val username: String,
    val rightcode: String,
    val rightno: String,
    val hosmain: String,
    val hossub: String

) {
    val flagservice = "03"
    val dateupdate: Timestamp = Timestamp(DateTime.now().plusHours(7).millis)

    val visitdate: Timestamp = Timestamp(homeVisit.time.plusHours(7).millis)
    val timestart: Time = Time(homeVisit.time.plusHours(7).millis)
    val timeend: Time = Time(homeVisit.time.plusHours(7).plusMinutes(5).millis)
    val symptoms = homeVisit.syntom
    val vitalcheck = homeVisit.result
    val weight = homeVisit.weight
    val height = homeVisit.height
    val waist = homeVisit.waist
    val ass = homeVisit.ass

    val bmilevel = when {
        homeVisit.bmi == null -> null
        homeVisit.bmi!!.isOverweight -> "5"
        homeVisit.bmi!!.isObese -> "5"
        homeVisit.bmi!!.isNormal -> "3"
        else -> "1"
    }

    val bpLevel = homeVisit.bloodPressureLevel
    val bp = homeVisit.bloodPressure
    val pressure = if (bp != null) "${bp.systolic.toInt()}/${bp.diastolic.toInt()}" else null
    val pressurelevel = when {
        bpLevel == null -> null
        bpLevel.isHigh -> "3"
        bpLevel.isPreHigh -> "2"
        else -> "1"
    }
    val temperature = homeVisit.bodyTemperature
    val flag18fileexpo = "2"
    val pulse = if (homeVisit.pulseRate == null) null else homeVisit.pulseRate
    val respri = if (homeVisit.respiratoryRate == null) null else homeVisit.respiratoryRate

    val timeservice: Int
        get() {
            return getTimeService(homeVisit.time.withZone(DateTimeZone.forOffsetHours(7)).hourOfDay)
        }

    fun getTimeService(houseOfDay: Int): Int {
        return if (houseOfDay in 9..15) 1 else 2
    }
}

fun HomeVisit.buildInsertData(
    pcucode: String,
    visitno: Long,
    pcucodeperson: String,
    pid: Long,
    username: String,
    rightcode: String,
    rightno: String,
    hosmain: String,
    hossub: String
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

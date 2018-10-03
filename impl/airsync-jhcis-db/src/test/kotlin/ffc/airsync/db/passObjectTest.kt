package ffc.airsync.db

import ffc.entity.gson.parseTo
import ffc.entity.healthcare.HomeVisit
import ffc.entity.healthcare.bloodPressureLevel
import org.amshove.kluent.`should be equal to`
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import java.sql.Time
import java.sql.Timestamp
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.TimeZone

class VisitData2(
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
            return getTimeService(homeVisit.time.toLocalDateTime().hourOfDay)
        }

    fun getTimeService(houseOfDay: Int): Int {
        return if (houseOfDay in 9..15) 1 else 2
    }
}

class passObjectTest {
    val json = """
        {
    "_id": {
        "${'$'}oid": "5bb1949aac308300049268d3"
    },
    "detail": "ทายา",
    "result": "น่าจะหายหลังจากนี้",
    "plan": "ทาา",
    "serviceType": {
        "translation": {},
        "id": "1A016",
        "name": "เยี่ยมผู้ป่วยโรคเอดส์"
    },
    "time": "2018-10-01T10:29:27.649+07:00",
    "endTime": "2018-10-01T10:34:27.649+07:00",
    "nextAppoint": "2018-10-05",
    "syntom": "มีผื่นเต็มตัว",
    "weight": 47,
    "height": 157,
    "waist": 75,
    "ass": 95,
    "bloodPressure": {
        "systolic": 110,
        "diastolic": 30
    },
    "pulseRate": 60,
    "respiratoryRate": 30,
    "bodyTemperature": 37,
    "diagnosises": [
        {
            "disease": {
                "icd10": "A00",
                "isEpimedic": false,
                "isChronic": false,
                "isNCD": false,
                "translation": {},
                "id": "6fdaf4b5713a4994b6e963f75ff3f501",
                "name": "อหิวาตกโรค"
            },
            "dxType": "PRINCIPLE_DX",
            "isContinued": false
        }
    ],
    "photosUrl": [],
    "providerId": "5bacabd910257c0004c52c06",
    "patientId": "5bacac9d10257c0004c55897",
    "id": "5bb1949aac308300049268d3",
    "type": "HomeVisit",
    "timestamp": "2018-10-01T10:29:27.649+07:00",
    "orgId": "5bacabd810257c0004c52bff"
}
    """.trimIndent()

    @Before
    fun setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(7))))
    }

    @Test
    fun timeServiceTest() {
        val homeVisit = json.parseTo<HomeVisit>()
        val visitdata =
            VisitData2(homeVisit, "00123", 123456789, "445566", 11122233343, "puy", "R45", "X99", "hotmain", "hossub")

        visitdata.timeservice `should be equal to` 1
    }
}

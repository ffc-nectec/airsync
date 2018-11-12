package ffc.airsync.db

import ffc.airsync.db.visit.InsertData
import ffc.entity.gson.parseTo
import ffc.entity.healthcare.HomeVisit
import org.amshove.kluent.`should be equal to`
import org.junit.Test

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

    @Test
    fun timeServiceTest() {
        val homeVisit = json.parseTo<HomeVisit>()
        val visitdata = InsertData(
                homeVisit,
                "00123",
                123456789,
                "445566",
                11122233343,
                "puy",
                "R45",
                "X99",
                "hotmain",
                "hossub")

        visitdata.timeservice `should be equal to` 1
    }
}

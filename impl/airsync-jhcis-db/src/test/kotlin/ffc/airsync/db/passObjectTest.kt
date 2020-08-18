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

package ffc.airsync.db

import ffc.airsync.visit.InsertData
import ffc.entity.gson.parseTo
import ffc.entity.healthcare.HealthCareService
import org.amshove.kluent.`should be equal to`
import org.junit.Test

class passObjectTest {

    val json = """
{
  "link": {
    "isSynced": true,
    "lastSync": "2018-11-21T14:23:28.289+07:00",
    "system": "JHICS",
    "keys": {
      "pcucode": "xxxxxx",
      "visitno": "yyyyyy"
    }
  },
  "id": "0f39f52bedeb4927a0793cdd4711a08e",
  "type": "HealthCareService",
  "timestamp": "2018-11-21T14:23:28.173+07:00",
  "nextAppoint": "2019-02-06",
  "syntom": "sleep",
  "suggestion": "suggestion",
  "weight": 80.3,
  "height": 190.5,
  "bloodPressure": {
    "systolic": 110,
    "diastolic": 90
  },
  "pulseRate": 62.2,
  "respiratoryRate": 20.4,
  "bodyTemperature": 37.1,
  "diagnosises": [
    {
      "disease": {
        "icd10": "I10.1",
        "isEpimedic": true,
        "isChronic": true,
        "isNCD": false,
        "type": "Icd10",
        "translation": {},
        "id": "5b69bf7ce5144493a3e57199d7461bd3",
        "name": "สมองเสื่อม"
      },
      "dxType": "OTHER",
      "isContinued": false
    },
    {
      "disease": {
        "icd10": "I10.1",
        "isEpimedic": true,
        "isChronic": true,
        "isNCD": false,
        "type": "Icd10",
        "translation": {},
        "id": "5b69bf7ce5144493a3e57199d7461bd3",
        "name": "สมองเสื่อม"
      },
      "dxType": "PRINCIPLE_DX",
      "isContinued": false
    }
  ],
  "photosUrl": [],
  "communityServices": [
    {
      "detail": "ทดสอบ ปกติทุกอย่าง ราบรื่น มากๆ",
      "plan": "กินอื่ม นอนหลับ ทานของมันให้น้อยลง ออกกำลังกาย",
      "serviceType": {
        "type": "ServiceType",
        "translation": {},
        "id": "654321",
        "name": "ให้อดข้าว"
      },
      "id": "26d15fe218d84b0a877853e6d585e4d6",
      "type": "HomeVisit",
      "timestamp": "2018-11-21T14:23:28.280+07:00"
    }
  ],
  "specialPPs": [],
  "time": "2018-09-06T14:28:15.000+07:00",
  "endTime": "2018-11-21T14:28:28.234+07:00",
  "location": {
    "type": "Point",
    "coordinates": [
      113.34,
      13
    ]
  },
  "providerId": "999999",
  "patientId": "000000"
}
    """.trimIndent()

    @Test
    fun timeServiceTest() {
        val homeVisit = json.parseTo<HealthCareService>()
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
            "hossub"
        )

        visitdata.timeservice `should be equal to` 1
    }
}

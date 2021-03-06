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

import ffc.airsync.utils.toTime
import ffc.entity.gson.toJson
import org.amshove.kluent.`should be equal to`
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import java.sql.Time
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.TimeZone

class DateFunctionTest {

    private val dateTime = DateTime(1536218895967)
    private val time = Time(dateTime.millis)

    @Before
    fun setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(7))))
    }

    @Test
    fun toTime() {
        "16:00:59".toTime().toString() `should be equal to` "16:00:59"
    }

    @Test
    fun condition() {
        ("14:28:15".toTime() < "16:00:00".toTime()) `should be equal to` true
    }

    @Test
    fun jodaTime() {
        dateTime.millis `should be equal to` 1536218895967
    }

    @Test
    fun sqlTime() {
        time.time `should be equal to` 1536218895967
    }

    @Test
    fun time() {
        time.toJson() `should be equal to` "7:28:15".toTime().toJson()
    }
}

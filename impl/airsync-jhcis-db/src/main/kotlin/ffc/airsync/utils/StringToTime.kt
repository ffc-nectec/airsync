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

package ffc.airsync.utils

import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import java.sql.Time
import java.text.SimpleDateFormat

fun String.toTime(): Time {
    val formatter = DateTimeFormat.forPattern("HH:mm:ss Z")
    val dt = formatter.parseDateTime(this + " +0000")

    return Time(dt.millis)
}

fun Time.toTime(): Time {
    val df = SimpleDateFormat("h:mm:ss")
    return df.format(this).toTime()
}

fun timeZone(plus: Int) = DateTimeZone.forOffsetHours(plus)

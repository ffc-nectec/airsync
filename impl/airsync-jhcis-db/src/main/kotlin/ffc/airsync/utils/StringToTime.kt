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

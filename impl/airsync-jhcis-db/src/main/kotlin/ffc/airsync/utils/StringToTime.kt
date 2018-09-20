package ffc.airsync.utils

import org.joda.time.format.DateTimeFormat
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.TimeZone

fun String.toTime(): Time {
    val formatter = DateTimeFormat.forPattern("HH:mm:ss")
    val dt = formatter.parseDateTime(this)
    return Time(dt.millis)
}

fun Time.toTime(): Time {
    val format = SimpleDateFormat("HH:mm:ss")
    format.timeZone = TimeZone.getDefault()
    return format.format(this).toTime()
}

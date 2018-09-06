package ffc.airsync.utils

import org.joda.time.format.DateTimeFormat
import java.sql.Time

fun String.toTime(): Time {
    val formatter = DateTimeFormat.forPattern("HH:mm:ss")
    val dt = formatter.parseDateTime(this)
    return Time(dt.millis)
}

fun Time.toTime(): Time {
    return this.toString().toTime()
}

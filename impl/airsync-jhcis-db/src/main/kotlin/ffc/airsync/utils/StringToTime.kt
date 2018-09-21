package ffc.airsync.utils

import org.joda.time.format.DateTimeFormat
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.TimeZone

fun String.toTime(): Time {
    val formatter = DateTimeFormat.forPattern("HH:mm:ss Z")
    val dt = formatter.parseDateTime(this + " +0700")

    return Time(dt.millis)
}

fun Time.toTime(): Time {
    val df = SimpleDateFormat("h:mm:ss")
    df.timeZone = TimeZone.getTimeZone("Asia/Bangkok")

    return df.format(this).toTime()
}

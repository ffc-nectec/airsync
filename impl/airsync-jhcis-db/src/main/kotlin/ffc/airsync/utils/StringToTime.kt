package ffc.airsync.utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.sql.Time
import java.util.regex.Pattern

fun String.toTime(): Time {
    val formatter = DateTimeFormat.forPattern("HH:mm:ss")
    val dt = formatter.parseDateTime(this)
    return Time(dt.millis)
}

fun Time.toTime(): Time {
    val p = Pattern.compile("""(1?[0-9]:[1-5]?[0-9]:[1-5]?[0-9])""")
    val result = p.matcher(DateTime(this).toLocalTime().toString())

    result.find()

    return result.group(1).toTime()
}

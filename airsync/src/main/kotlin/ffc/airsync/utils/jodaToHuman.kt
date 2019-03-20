package ffc.airsync.utils

import org.joda.time.DateTime
import java.util.Locale

fun DateTime.toBuddistString(
    pattern: String = "d MMM yyyy HH:mm น.",
    locale: Locale = Locale("th", "TH")
) = this.plusYears(543).toString(pattern, locale)

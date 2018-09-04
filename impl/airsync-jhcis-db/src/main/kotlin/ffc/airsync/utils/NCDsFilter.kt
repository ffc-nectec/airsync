package ffc.airsync.utils

import java.util.regex.Pattern

fun String.ncdsFilter(): Boolean {
    val ncdFilterList = arrayListOf<String>().apply {
        add("""^e10\.\d$""")
        add("""^e11\.\d$""")
        add("""^i10$""")
    }

    ncdFilterList.forEach {
        val pattern = Pattern.compile(it, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(this)
        if (matcher.find()) return true
    }
    return false
}

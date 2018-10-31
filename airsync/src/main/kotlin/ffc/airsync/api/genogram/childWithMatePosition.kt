package ffc.airsync.api.genogram

internal fun childWithMatePosition(familyPosition: String): String {
    return when (familyPosition) {
        "1", "2" -> "3"
        "3" -> "จ"
        "6", "7" -> "1"
        "8", "9" -> "2"
        "i", "j" -> "6"
        "k", "l" -> "7"
        "m", "n" -> "8"
        "o", "p" -> "9"
        else -> ""
    }
}

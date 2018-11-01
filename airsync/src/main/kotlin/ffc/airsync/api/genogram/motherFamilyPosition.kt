package ffc.airsync.api.genogram

internal fun motherFamilyPosition(familyPosition: String): String {
    return when (familyPosition) {
        "1" -> "7"
        "2" -> "9"
        "3" -> "2"
        "7" -> "l"
        "6" -> "j"
        "8" -> "n"
        "9" -> "p"
        else -> ""
    }
}

package ffc.airsync.api.genogram

internal fun fatherFamilyPosition(familyPosition: String): String {

    return when (familyPosition) {
        "1" -> "6"
        "2" -> "8"
        "3" -> "1"
        "7" -> "k"
        "6" -> "i"
        "8" -> "m"
        "9" -> "o"
        else -> ""
    }
}

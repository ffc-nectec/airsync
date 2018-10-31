package ffc.airsync.api.genogram

internal fun fatherFamilyPosition(familyPosition: String): String {

    val fatherPosition: String
    val position = familyPosition
    when (position) {
        "1" -> fatherPosition = "6"
        "2" -> fatherPosition = "8"
        "3" -> fatherPosition = "1"
        "7" -> fatherPosition = "k"
        "6" -> fatherPosition = "i"
        "8" -> fatherPosition = "m"
        "9" -> fatherPosition = "o"
        else -> fatherPosition = ""
    }
    return fatherPosition
}

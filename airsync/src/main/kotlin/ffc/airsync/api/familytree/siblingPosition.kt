package ffc.airsync.api.familytree

fun siblingPosition(familyPosition: String): String {
    if (familyPosition.isEmpty())
        return ""

    val siblingPosition: String?
    when (familyPosition) {
        "1" -> siblingPosition = " (familyposition = \'ก\' OR familyposition = \'ข\')"
        "2" -> siblingPosition = " (familyposition = \'ค\' OR familyposition = \'ง\')"
        else -> siblingPosition = ""
    }
    return siblingPosition
}

package ffc.airsync.api.genogram

internal fun siblingPosition(familyPosition: String): List<String> {
    if (familyPosition.isEmpty())
        return listOf()

    return when (familyPosition) {
        "1" -> listOf("ก", "ข")
        "2" -> listOf("ค", "ง")
        else -> listOf()
    }
}

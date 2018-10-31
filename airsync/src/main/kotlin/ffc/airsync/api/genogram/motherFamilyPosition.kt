package ffc.airsync.api.genogram

internal fun motherFamilyPosition(familyPosition: String): String {
    val motherPosition: String
    when {
        familyPosition.equals("1", ignoreCase = true) -> motherPosition = "7"
        familyPosition.equals("2", ignoreCase = true) -> motherPosition = "9"
        familyPosition.equals("3", ignoreCase = true) -> motherPosition = "2"
        familyPosition.equals("7", ignoreCase = true) -> motherPosition = "l"
        familyPosition.equals("6", ignoreCase = true) -> motherPosition = "j"
        familyPosition.equals("8", ignoreCase = true) -> motherPosition = "n"
        familyPosition.equals("9", ignoreCase = true) -> motherPosition = "p"
        else -> motherPosition = ""
    }
    return motherPosition
}

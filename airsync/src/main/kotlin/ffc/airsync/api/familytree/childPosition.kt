package ffc.airsync.api.familytree

fun childPosition(familyPosition: String, foundMate: Boolean): String {
    // leader child
    // child of childWithMate
    // else if (familyPosition.equalsIgnoreCase("3"))
    // childPosition = "จ";
    // child of leader child
    // else if (familyPosition.equalsIgnoreCase("4"))
    // childPosition = "ฉ";
    // child of mate child
    // else if (familyPosition.equalsIgnoreCase("5"))
    // childPosition = "ช";
    // Mate child
    return if (familyPosition == "1")
        if (foundMate) {
            "4"
        } else {
            "3"
        }
    else if (familyPosition == "2")
        "5"
    else
        ""
}

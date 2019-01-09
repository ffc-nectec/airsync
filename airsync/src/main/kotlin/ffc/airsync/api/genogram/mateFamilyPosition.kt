package ffc.airsync.api.genogram

internal fun mateFamilyPosition(familyPosition: String): String {
    // หัวหน้าครอบครัว กับ
    // คู๋สมรส
    // println("find mate's familyposition by his/her familypositon =$familyPosition")
    // println((familyPosition + " = \"1\" " + (familyPosition == "1")))
    // บุตร บุตรสะใภ้ บุตรเขย
    // else if ((familyPosition.equalsIgnoreCase("3")) && (sex == 1))
    // mateFamilyPostion = "ฑ";
    // else if ((familyPosition.equalsIgnoreCase("3")) && (sex == 2))
    // mateFamilyPostion = "ฒ";
    // อื่น ๆ
    // ตา ยาย คู่สมรส
    // ปู่ ย่า คู่สมรส
    // พ่อแม่คู่สมรส
    // ตา ยาย หัวหน้าครอบครัว
    // ปู่ ย่า หัวหน้าครอบครัว
    // พ่อแม่หัวหน้าครอบครัว
    // println((" found mate's familyposition with " + mateFamilyPostion))
    return when (familyPosition) {
        "1" -> "2"
        "2" -> "1"
        "6" -> "7"
        "7" -> "6"
        "i" -> "j"
        "j" -> "i"
        "k" -> "l"
        "l" -> "k"
        "8" -> "9"
        "9" -> "8"
        "m" -> "n"
        "n" -> "m"
        "o" -> "p"
        "p" -> "o"
        else -> ""
    }
}

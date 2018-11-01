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
    val mateFamilyPostion: String
    when (familyPosition) {
        "1" -> mateFamilyPostion = "2"
        "2" -> mateFamilyPostion = "1"
        "6" -> mateFamilyPostion = "7"
        "7" -> mateFamilyPostion = "6"
        "i" -> mateFamilyPostion = "j"
        "j" -> mateFamilyPostion = "i"
        "k" -> mateFamilyPostion = "l"
        "l" -> mateFamilyPostion = "k"
        "8" -> mateFamilyPostion = "9"
        "9" -> mateFamilyPostion = "8"
        "m" -> mateFamilyPostion = "n"
        "n" -> mateFamilyPostion = "m"
        "o" -> mateFamilyPostion = "p"
        "p" -> mateFamilyPostion = "o"
        else -> mateFamilyPostion = ""
    }
    return mateFamilyPostion
}

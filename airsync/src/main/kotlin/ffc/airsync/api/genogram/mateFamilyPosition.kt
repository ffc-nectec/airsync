package ffc.airsync.api.genogram

import ffc.airsync.api.genogram.JhcisFamilyPosition.`คู่สมรส(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ตา(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ตา(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`บิดา(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`บิดา(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ปู่(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ปู่(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`มารดา(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`มารดา(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ยาย(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ยาย(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ย่า(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ย่า(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`หัวหน้าครอบครัว`

internal fun mateFamilyPosition(familyPosition: JhcisFamilyPosition): JhcisFamilyPosition? {
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
        `หัวหน้าครอบครัว` -> `คู่สมรส(ของ หนครอบครัว)`
        `คู่สมรส(ของ หนครอบครัว)` -> `หัวหน้าครอบครัว`
        `บิดา(ของ หนครอบครัว)` -> `มารดา(ของ หนครอบครัว)`
        `มารดา(ของ หนครอบครัว)` -> `บิดา(ของ หนครอบครัว)`
        `ปู่(ของ หนครอบครัว)` -> `ย่า(ของ หนครอบครัว)`
        `ย่า(ของ หนครอบครัว)` -> `ปู่(ของ หนครอบครัว)`
        `ตา(ของ หนครอบครัว)` -> `ยาย(ของ หนครอบครัว)`
        `ยาย(ของ หนครอบครัว)` -> `ตา(ของ หนครอบครัว)`
        `บิดา(ของ คู่สมรส)` -> `มารดา(ของ คู่สมรส)`
        `มารดา(ของ คู่สมรส)` -> `บิดา(ของ คู่สมรส)`
        `ปู่(ของ คู่สมรส)` -> `ย่า(ของ คู่สมรส)`
        `ย่า(ของ คู่สมรส)` -> `ปู่(ของ คู่สมรส)`
        `ตา(ของ คู่สมรส)` -> `ยาย(ของ คู่สมรส)`
        `ยาย(ของ คู่สมรส)` -> `ตา(ของ คู่สมรส)`
        else -> null
    }
}

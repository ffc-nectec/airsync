package ffc.airsync.api.genogram

import ffc.airsync.api.genogram.JhcisFamilyPosition.`คู่สมรส(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ตา(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ตา(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`บิดา(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`บิดา(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`บุตร`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ปู่(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ปู่(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`มารดา(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`มารดา(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ยาย(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ยาย(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ย่า(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ย่า(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`หัวหน้าครอบครัว`
import ffc.airsync.api.genogram.JhcisFamilyPosition.หลาน

internal fun childWithMatePosition(familyPosition: JhcisFamilyPosition): JhcisFamilyPosition? {
    return when (familyPosition) {
        `หัวหน้าครอบครัว`, `คู่สมรส(ของ หนครอบครัว)` -> `บุตร`
        `บุตร` -> หลาน
        `บิดา(ของ หนครอบครัว)`, `มารดา(ของ หนครอบครัว)` -> `หัวหน้าครอบครัว`
        `บิดา(ของ คู่สมรส)`, `มารดา(ของ คู่สมรส)` -> `คู่สมรส(ของ หนครอบครัว)`
        `ปู่(ของ หนครอบครัว)`, `ย่า(ของ หนครอบครัว)` -> `บิดา(ของ หนครอบครัว)`
        `ตา(ของ หนครอบครัว)`, `ยาย(ของ หนครอบครัว)` -> `มารดา(ของ หนครอบครัว)`
        `ปู่(ของ คู่สมรส)`, `ย่า(ของ คู่สมรส)` -> `บิดา(ของ คู่สมรส)`
        `ตา(ของ คู่สมรส)`, `ยาย(ของ คู่สมรส)` -> `มารดา(ของ คู่สมรส)`
        else -> null
    }
}

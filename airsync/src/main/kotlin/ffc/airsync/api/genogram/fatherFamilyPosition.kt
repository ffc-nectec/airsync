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
import ffc.airsync.api.genogram.JhcisFamilyPosition.`หัวหน้าครอบครัว`

internal fun fatherFamilyPosition(familyPosition: JhcisFamilyPosition): JhcisFamilyPosition? {

    return when (familyPosition) {
        `หัวหน้าครอบครัว` -> `บิดา(ของ หนครอบครัว)`
        `คู่สมรส(ของ หนครอบครัว)` -> `บิดา(ของ คู่สมรส)`
        `บุตร` -> `หัวหน้าครอบครัว`
        `มารดา(ของ หนครอบครัว)` -> `ตา(ของ หนครอบครัว)`
        `บิดา(ของ หนครอบครัว)` -> `ปู่(ของ หนครอบครัว)`
        `บิดา(ของ คู่สมรส)` -> `ปู่(ของ คู่สมรส)`
        `มารดา(ของ คู่สมรส)` -> `ตา(ของ คู่สมรส)`
        else -> null
    }
}

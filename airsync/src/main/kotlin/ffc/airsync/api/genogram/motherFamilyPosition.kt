package ffc.airsync.api.genogram

import ffc.airsync.api.genogram.JhcisFamilyPosition.`คู่สมรส(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`บิดา(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`บิดา(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`บุตร`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`มารดา(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`มารดา(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ยาย(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ยาย(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ย่า(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`ย่า(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`หัวหน้าครอบครัว`

internal fun motherFamilyPosition(familyPosition: JhcisFamilyPosition): JhcisFamilyPosition? {
    return when (familyPosition) {
        `หัวหน้าครอบครัว` -> `มารดา(ของ หนครอบครัว)`
        `คู่สมรส(ของ หนครอบครัว)` -> `มารดา(ของ คู่สมรส)`
        `บุตร` -> `คู่สมรส(ของ หนครอบครัว)`
        `มารดา(ของ หนครอบครัว)` -> `ยาย(ของ หนครอบครัว)`
        `บิดา(ของ หนครอบครัว)` -> `ย่า(ของ หนครอบครัว)`
        `บิดา(ของ คู่สมรส)` -> `ย่า(ของ คู่สมรส)`
        `มารดา(ของ คู่สมรส)` -> `ยาย(ของ คู่สมรส)`
        else -> null
    }
}

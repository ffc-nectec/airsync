package ffc.airsync.api.genogram

import ffc.airsync.api.genogram.JhcisFamilyPosition.`คู่สมรส(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`บุตร(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`บุตร(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`บุตร`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`หัวหน้าครอบครัว`

internal fun childPosition(familyPosition: JhcisFamilyPosition, foundMate: Boolean): JhcisFamilyPosition? {
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
    return if (familyPosition == `หัวหน้าครอบครัว`)
        if (foundMate) {
            `บุตร(ของ หนครอบครัว)`
        } else {
            `บุตร`
        }
    else if (familyPosition == `คู่สมรส(ของ หนครอบครัว)`)
        `บุตร(ของ คู่สมรส)`
    else
        null
}

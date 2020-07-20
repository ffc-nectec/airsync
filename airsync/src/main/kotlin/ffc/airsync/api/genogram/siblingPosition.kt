package ffc.airsync.api.genogram

import ffc.airsync.api.genogram.JhcisFamilyPosition.`คู่สมรส(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`น้อง(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`น้อง(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`พี่(ของ คู่สมรส)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`พี่(ของ หนครอบครัว)`
import ffc.airsync.api.genogram.JhcisFamilyPosition.`หัวหน้าครอบครัว`

internal fun siblingPosition(familyPosition: JhcisFamilyPosition?): List<JhcisFamilyPosition> {
    if (familyPosition == null)
        return listOf()

    return when (familyPosition) {
        `หัวหน้าครอบครัว` -> listOf(`พี่(ของ หนครอบครัว)`, `น้อง(ของ หนครอบครัว)`)
        `คู่สมรส(ของ หนครอบครัว)` -> listOf(`พี่(ของ คู่สมรส)`, `น้อง(ของ คู่สมรส)`)
        else -> listOf()
    }
}

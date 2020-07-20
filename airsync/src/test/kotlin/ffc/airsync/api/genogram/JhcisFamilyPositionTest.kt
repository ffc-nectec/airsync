package ffc.airsync.api.genogram

import ffc.airsync.api.genogram.JhcisFamilyPosition.`คู่สมรส(ของ หนครอบครัว)`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.junit.Test

class JhcisFamilyPositionTest {

    @Test
    fun getValue() {
        `คู่สมรส(ของ หนครอบครัว)`.value `should equal` '2'
        JhcisFamilyPosition.valueOf('2') `should equal` `คู่สมรส(ของ หนครอบครัว)`
        JhcisFamilyPosition.valueOf('>') `should equal` null
        `คู่สมรส(ของ หนครอบครัว)`.value.toString() `should be equal to` "2"
    }
}

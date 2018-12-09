package th.`in`.ffc.airsync.logreader.getkey

import org.amshove.kluent.`should be equal to`
import org.junit.Test

class UpdateHouseTest {

    @Test
    fun getKeyFromUpdateHouse() {
        val logLine = """UPDATE `house` SET `hno`='78/5' WHERE  `pcucode`='07934' AND `hcode`=305"""
        val updateProcess = Update()

        updateProcess.get(logLine).first() `should be equal to` """`pcucode`='07934' AND `hcode`=305"""
    }
}

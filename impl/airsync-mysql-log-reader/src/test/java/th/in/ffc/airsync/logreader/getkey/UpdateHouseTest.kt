package th.`in`.ffc.airsync.logreader.getkey

import org.amshove.kluent.`should be equal to`
import org.junit.Test

class UpdateHouseTest {

    val houseMaps = arrayListOf<String>().apply {
        add("`house`")
        add("house")
    }

    @Test
    fun getKeyFromUpdateHouse() {
        val logLine = """UPDATE `house` SET `hno`='78/5' WHERE  `pcucode`='07934' AND `hcode`=305"""
        val updateProcess = Update(houseMaps)

        updateProcess.get(logLine) `should be equal to` """`pcucode`='07934' AND `hcode`=305"""
    }
}

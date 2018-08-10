package th.`in`.ffc.airsync.logreader

import org.amshove.kluent.`should be equal to`
import org.junit.Test

class DebugTest {
    fun LogReaderV1() {
        val readTextFile = LogReaderV1(Config.logfilepath, true, 100)

        readTextFile.setListener {
            println(it.log)
        }

        readTextFile.process()

        while (true)
            Thread.sleep(3000)
    }

    fun LogReaderV2() {

        val logReader = LogReaderV2(Config.logfilepath) { tableName:
                                                          String, keyWhere: String ->
            println("Table:$tableName,Where:$keyWhere")
        }

        logReader.start()
        while (true)
            Thread.sleep(3000)
    }

    @Test
    fun EmptyToInt() {
        "0".toInt() `should be equal to` 0
    }
}

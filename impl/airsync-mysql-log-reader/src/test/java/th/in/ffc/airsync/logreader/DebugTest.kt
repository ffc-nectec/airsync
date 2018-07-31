package th.`in`.ffc.airsync.logreader

import org.junit.Test

class DebugTest {
    @Test
    fun readLogRealtime() {
        val readTextFile = LogReaderV1(Config.logfilepath, true, 100)

        readTextFile.setListener {
            println(it.log)
        }

        readTextFile.process()

        while (true)
            Thread.sleep(3000)
    }

    @Test
    fun readLogKot() {

        val logReader = LogReaderV2(Config.logfilepath, onLogInput = { it: QueryRecord, tableName: String, keyWhere: String ->
            println("L:${it.linenumber},T:${it.time},Table:$tableName,Where:$keyWhere,SQL:${it.log}")
        })

        logReader.run()
        while (true)
            Thread.sleep(3000)
    }

}

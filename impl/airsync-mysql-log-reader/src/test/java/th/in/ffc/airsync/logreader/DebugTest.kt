package th.`in`.ffc.airsync.logreader

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

        val logReader = LogReaderV2(Config.logfilepath) { it: QueryRecord,
                                                                       tableName:
                                                                       String, keyWhere: String ->
            println("L:${it.linenumber},T:${it.time},Table:$tableName,Where:$keyWhere,SQL:${it.log}")
        }

        logReader.start()
        while (true)
            Thread.sleep(3000)
    }
}

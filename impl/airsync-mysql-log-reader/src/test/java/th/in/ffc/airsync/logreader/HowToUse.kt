package th.`in`.ffc.airsync.logreader

import org.junit.Ignore

class HowToUse {

    val filter = hashMapOf<String, List<String>>().apply {
        put("house", listOf("house", "`house`"))
    }

    @Ignore("How to use TextFileReader")
    fun exampleTextFileReader() {
        val readTextFile = TextFileReader(Config.logfilepath, true, 100)

        readTextFile.setListener(LineManage("temp.a")) {
            println(it.log)
        }

        readTextFile.process()

        while (true)
            Thread.sleep(3000)
    }

    @Ignore("How to use LogReader")
    fun exampleLogReader() {

        val logReader = LogReader(Config.logfilepath, tableMaps = filter) { tableName, keyWhere ->
            println("Table:$tableName,Where:$keyWhere")
        }

        logReader.start()
        while (true)
            Thread.sleep(3000)
    }
}

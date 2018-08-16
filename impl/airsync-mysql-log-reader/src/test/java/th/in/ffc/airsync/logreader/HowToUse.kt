package th.`in`.ffc.airsync.logreader

import org.junit.Ignore

class HowToUse {

    @Ignore("How to use TextFileReader")
    fun exampleTextFileReader() {
        val readTextFile = TextFileReader(Config.logfilepath, true, 100)

        readTextFile.setListener {
            println(it.log)
        }

        readTextFile.process()

        while (true)
            Thread.sleep(3000)
    }

    @Ignore("How to use LogReader")
    fun exampleLogReader() {

        val logReader = LogReader(Config.logfilepath) { tableName, keyWhere ->
            println("Table:$tableName,Where:$keyWhere")
        }

        logReader.start()
        while (true)
            Thread.sleep(3000)
    }
}

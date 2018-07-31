package th.`in`.ffc.airsync.logreader

import th.`in`.ffc.airsync.logreader.filter.CreateHash
import th.`in`.ffc.airsync.logreader.filter.Filters
import th.`in`.ffc.airsync.logreader.filter.GetTimeFilter
import th.`in`.ffc.airsync.logreader.filter.NowFilter
import th.`in`.ffc.airsync.logreader.filter.QueryFilter
import java.util.Arrays
import java.util.regex.Pattern

class LogReaderV2(val logfilepath: String, val onLogInput: (line: QueryRecord, tableName: String) -> Unit, val delay: Long = 100) {

    val startWithBeforeTable = arrayListOf<String>().apply {
        add("INSERT INTO")
        add("UPDATE")
        add("DELETE FROM")
        add("insert into")
        add("update")
        add("delete from")
    }


    private var loadFilters = Arrays.asList<Filters>(
            GetTimeFilter(Config.timePattern),
            QueryFilter(Config.logpattern),
            NowFilter(),
            CreateHash()
    )

    private fun readSingleLogFileRealTime() {
        val readLogFile = LogReaderV1(logfilepath, true, delay)
        readLogFile.setListener { record ->

            loadFilters.forEach {
                it.process(record)
            }
            if (record.log != "") {
                onLogInput(record, getTableInLogLine(record.log))
            }
        }
        readLogFile.process()

    }

    private fun getTableInLogLine(logLine: String): String {
        for (it in startWithBeforeTable) {
            if (logLine.startsWith(it)) {
                val pattern = Pattern.compile("""^$it `?([\w\d]+)`?.+""")
                val table = pattern.matcher(logLine)
                table.find()
                return table.group(1)
            }
        }
        return ""
    }

    fun run() {
        val thread = Thread { readSingleLogFileRealTime() }
        thread.start()
    }
}

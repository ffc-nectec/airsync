package th.`in`.ffc.airsync.logreader

import th.`in`.ffc.airsync.logreader.filter.CreateHash
import th.`in`.ffc.airsync.logreader.filter.Filters
import th.`in`.ffc.airsync.logreader.filter.GetTimeFilter
import th.`in`.ffc.airsync.logreader.filter.NowFilter
import th.`in`.ffc.airsync.logreader.filter.QueryFilter
import th.`in`.ffc.airsync.logreader.getkey.GetWhere
import th.`in`.ffc.airsync.logreader.getkey.Update
import java.util.Arrays
import java.util.regex.Pattern

class LogReaderV2(val logfilepath: String, val onLogInput: (line: QueryRecord, tableName: String, keyWhere: String) -> Unit, val delay: Long = 100) {

    val startWithBeforeTable = arrayListOf<String>().apply {
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

    val keyFilters = arrayListOf<GetWhere>().apply {
        add(Update())
    }

    private fun readSingleLogFileRealTime() {
        val readLogFile = LogReaderV1(logfilepath, true, delay)
        readLogFile.setListener { record ->

            loadFilters.forEach {
                it.process(record)
            }

            var key = ""
            for (keyFilter in keyFilters) {
                key = keyFilter.get(record.log)
                if (key != "") {
                    break
                }
            }


            if (record.log != "") {
                onLogInput(record, getTableInLogLine(record.log), key)
            }
        }
        readLogFile.process()

    }

    private fun getTableInLogLine(logLine: String): String {
        for (it in startWithBeforeTable) {
            if (logLine.startsWith(it)) {
                val pattern = Pattern.compile("""^$it `?([\w\d]+)`?.+""", Pattern.CASE_INSENSITIVE)
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

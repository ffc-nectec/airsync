package th.`in`.ffc.airsync.logreader

import ffc.airsync.db.DatabaseWatcherDao
import th.`in`.ffc.airsync.logreader.filter.CreateHash
import th.`in`.ffc.airsync.logreader.filter.Filters
import th.`in`.ffc.airsync.logreader.filter.GetTimeFilter
import th.`in`.ffc.airsync.logreader.filter.NowFilter
import th.`in`.ffc.airsync.logreader.filter.QueryFilter
import th.`in`.ffc.airsync.logreader.getkey.GetWhere
import th.`in`.ffc.airsync.logreader.getkey.Update
import java.util.Arrays
import java.util.regex.Pattern

class LogReaderV2(
    val filepath: String,
    val onLogInput: (line: QueryRecord, tableName: String, keyWhere: String) -> Unit,
    val delay: Long = 300
) : DatabaseWatcherDao {

    override fun start() {
        val thread = Thread { readSingleLogFileRealTime() }
        thread.start()
    }

    val tableQuery = arrayListOf<String>().apply {
        add("house")
        add("person")
        add("personchronic")
    }

    val startWithBeforeTable = arrayListOf<String>().apply {
        add("insert into")
        add("update")
        add("delete from")
        add("insert into".toUpperCase())
        add("update".toUpperCase())
        add("delete from".toUpperCase())
    }

    private var loadFilters = Arrays.asList<Filters>(
            GetTimeFilter(Config.timePattern),
            QueryFilter(Config.logpattern),
            NowFilter(),
            CreateHash()
    )

    private val keyFilters = arrayListOf<GetWhere>().apply {
        add(Update())
    }

    private fun readSingleLogFileRealTime() {
        val readLogFile = LogReaderV1(filepath, true, delay)
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
                val tableInLog = getTableInLogLine(record.log)
                onLogInput(record, tableInLog, key)
                for (it in tableQuery) {
                    if (tableInLog.contains(it)) {
                        break
                    }
                }
            }
        }
        readLogFile.process()
    }

    private fun getTableInLogLine(logLine: String): String {
        for (it in startWithBeforeTable) {
            if (logLine.startsWith(it)) {
                val pattern = Pattern.compile("""^$it +(`?[\w\d]+`?(\.?`?[\w\d]+`?)?) ?""", Pattern.CASE_INSENSITIVE)
                val tableMatch = pattern.matcher(logLine.trim())
                tableMatch.find()
                var table = ""
                try {
                    table = tableMatch.group(1)
                } catch (ignore: java.lang.IllegalStateException) {
                    println("\n\nIg $it + $logLine")
                }
                return table
            }
        }
        return ""
    }
}

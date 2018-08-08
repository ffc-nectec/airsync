package th.`in`.ffc.airsync.logreader

import ffc.airsync.db.DatabaseWatcherDao
import th.`in`.ffc.airsync.logreader.filter.*
import th.`in`.ffc.airsync.logreader.getkey.GetWhere
import th.`in`.ffc.airsync.logreader.getkey.UpdateHouse
import java.util.*
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

    /*private val tableQuery = arrayListOf<String>().apply {
        add("`house`")
        add("house")
    }*/

    private val tableMaps = arrayListOf<TableMaps>().apply {
        val houseMaps = arrayListOf<String>().apply {
            add("`house`")
            add("house")
        }

        add(TableMaps("house", houseMaps))
    }

    private val startWithBeforeTable = arrayListOf<String>().apply {
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
        add(UpdateHouse())
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
                for (map in tableMaps) {
                    for (value in map.tableNameList) {
                        if (tableInLog.contains(value)) {
                            onLogInput(record, value, key)
                            break
                        }
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

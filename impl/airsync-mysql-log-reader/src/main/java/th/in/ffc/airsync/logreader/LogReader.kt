package th.`in`.ffc.airsync.logreader

import th.`in`.ffc.airsync.logreader.filter.CreateHash
import th.`in`.ffc.airsync.logreader.filter.Filters
import th.`in`.ffc.airsync.logreader.filter.GetTimeFilter
import th.`in`.ffc.airsync.logreader.filter.NowFilter
import th.`in`.ffc.airsync.logreader.filter.QueryFilter
import java.util.Arrays
import java.util.regex.Pattern

class LogReader(val logfilepath: String, val onLogInput: (line: QueryRecord, tableName: String) -> Unit, val delay: Long = 100) {

    val queryTable = arrayListOf<String>().apply {
        add("INSERT INTO")
        add("UPDATE")
        add("DELETE FROM")
        add("insert into")
        add("update")
        add("delete from")
    }
    val selectHouse = arrayListOf<String>()

    private var filters = Arrays.asList<Filters>(
            GetTimeFilter(Config.timePattern),
            QueryFilter(Config.logpattern),
            NowFilter(),
            CreateHash()
    )

    private fun processSingle() {
        val readLogFile = ReadLogFile(logfilepath, true, delay)
        readLogFile.setListener { record ->

            filters.forEach {
                it.process(record)
            }
            if (record.log != "") {
                onLogInput(record, filterTable(record.log))
            }
        }
        readLogFile.process()

    }

    private fun filterTable(logLine: String): String {
        for (it in queryTable) {
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
        val thread = Thread { processSingle() }
        thread.start()
    }
}

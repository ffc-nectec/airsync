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

class LogReader(
    val filepath: String,
    val delay: Long = 300,
    val isTest: Boolean = false,
    val onLogInput: (tableName: String, keyWhere: String) -> Unit
) : DatabaseWatcherDao {

    private var lineManage: LineManage

    init {
        if (isTest) {
            lineManage = LineManage("logTest.cfg")
        } else {
            lineManage = LineManage()
        }
    }

    override fun start() {
        val thread = Thread { readSingleLogFileRealTime() }
        thread.start()
    }

    private val tableMaps = HashMap<String, ArrayList<String>>().apply {
        val houseMaps = arrayListOf<String>().apply {
            add("`house`")
            add("house")
        }

        put("house", houseMaps)
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
        add(Update(tableMaps["house"]!!.toList()))
    }

    private fun readSingleLogFileRealTime() {
        val readLogFile = TextFileReader(filepath, true, delay)
        readLogFile.setListener { record ->
            if (record.linenumber > lineManage.getLastLineNumber()) {
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
                    val tableInLog = getTable(record.log)
                    for ((k, v) in tableMaps) {
                        when (k) {
                            "house" ->
                                for (value in v) {
                                    if (tableInLog.contains(value)) {
                                        onLogInput(k, key)
                                        break
                                    }
                                }
                        }
                    }
                    lineManage.setLastLineNumber(record.linenumber)
                }
            }
        }
        readLogFile.process()
    }

    private fun getTable(logLine: String): String {
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

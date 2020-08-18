/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package th.`in`.ffc.airsync.logreader

import ffc.airsync.db.DatabaseWatcherDao
import th.`in`.ffc.airsync.logreader.filter.CreateHash
import th.`in`.ffc.airsync.logreader.filter.Filters
import th.`in`.ffc.airsync.logreader.filter.GetTimeFilter
import th.`in`.ffc.airsync.logreader.filter.NowFilter
import th.`in`.ffc.airsync.logreader.filter.QueryFilter
import th.`in`.ffc.airsync.logreader.getkey.GetWhere
import th.`in`.ffc.airsync.logreader.getkey.Insert
import th.`in`.ffc.airsync.logreader.getkey.Update
import java.util.Arrays
import java.util.regex.Pattern

/**
 * ใช้สำหรับอ่าน Log mysql จะถูกกระตุ้นให้เรียก callback onLogInput
 * เมื่อมี log เกิดขึ้นตามที่ระบุไว้ใน Filter
 * @param filepath ที่อยู่ของ log file
 * @param delay ค่าหน่วงเวลาในการตรวจสอบ
 * @param isTest ใช้สำหรับกำหนดที่เก็บบันทึก config ของตัว logreader
 * @param tablesPattern Filter สำหรับกรอง Table เช่น "person", "`person`"
 * @param onLogInput callback จะถูกเรียกเมื่อพบ log ที่ระบุไว้ใน tableMaps
 */
class LogReader(
    private val filepath: String,
    val delay: Long = 300,
    val isTest: Boolean = false,
    val tablesPattern: Map<String, List<String>>,
    val lookupIsShutdown: () -> Boolean = { false },
    val onLogInput: (tableName: String, keyWhere: List<String>) -> Unit
) : DatabaseWatcherDao {
    private var lineManage: LineManage = when {
        isTest -> LineManage("logTest.cfg")
        else -> LineManage("log.cfg")
    }
    private val keyFilters = arrayListOf<GetWhere>()
    private val shutdown: () -> Boolean = lookupIsShutdown
    private var readLogFile: TextFileReader? = null

    init {
        keyFilters.add(Update())
        keyFilters.add(Insert())
    }

    override fun start() {
        val thread = Thread { readSingleLogFileRealTime() }
        thread.start()
    }

    override var isShutdown: Boolean
        get() = shutdown()
        set(value) {
        }
    private val prefixSql = arrayListOf<String>().apply {
        add("insert into")
        add("update")
        add("delete from")
        add("Insert into")
        add("Update")
        add("Delete from")
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

    private fun readSingleLogFileRealTime() {
        readLogFile = TextFileReader(filepath, true, delay)
        readLogFile?.setListener(lineManage) { record ->
            if (shutdown()) readLogFile?.stop()
            if (record.linenumber > lineManage.getLastLineNumber()) {
                loadFilters.forEach {
                    it.process(record)
                }

                if ((record.linenumber % 500000) == 0L)
                    lineManage.setLastLineNumber(record.linenumber)

                if (record.log.isNotBlank()) {
                    val tableInLog = getTable(record.log)
                    val key = getPrimaryKey(record.log)
                    callBack(tableInLog, key)

                    lineManage.setLastLineNumber(record.linenumber)
                }
            }
        }
        readLogFile?.process()
    }

    private fun callBack(tableNameStringInLog: String, sqlWhereString: List<String>) {

        tablesPattern.forEach loop@{ tableKeyName, tablePattern ->
            tablePattern.forEach { pattern ->
                if (tableNameStringInLog == pattern) {
                    onLogInput(tableKeyName, sqlWhereString)
                    return@loop
                }
            }
        }
    }

    private fun getPrimaryKey(record: String): List<String> {
        var key1 = listOf<String>()
        keyFilters.forEach {
            if (key1.isEmpty())
                key1 = it.get(record)
        }
        return key1
    }

    /**
     * ดึงชื่อตารางออกมาจาก บรรทัดการ query
     */
    private fun getTable(logLine: String): String {
        prefixSql.forEach { it ->
            if (logLine.startsWith(it)) {
                val pattern = Pattern.compile("""^$it +(`?[\w\d]+`?(\.?`?[\w\d]+`?)?) ?""", Pattern.CASE_INSENSITIVE)
                val tableMatch = pattern.matcher(logLine.trim())
                tableMatch.find()
                return tableMatch.group(1) ?: ""
            }
        }
        return ""
    }
}

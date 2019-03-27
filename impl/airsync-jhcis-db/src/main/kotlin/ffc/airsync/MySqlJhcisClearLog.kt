package ffc.airsync

import ffc.airsync.utils.startJhcisMySql
import ffc.airsync.utils.stopJhcisMySql
import java.io.File

class MySqlJhcisClearLog {
    private val mySqlStop = "net stop mysql_jhcis"
    private val mySqlStart = "net start mysql_jhcis"
    private val logFile = File("C:\\Program Files\\JHCIS\\MySQL\\data\\jlog.log")

    fun clearLog() {
        stopJhcisMySql()
        logFile.delete()
        startJhcisMySql()
    }
}

package ffc.airsync.mysqlconfig

import mysql.config.read.ReadOptionMyini
import mysql.config.write.WriteOptionMyini
import java.io.File
import java.util.prefs.Preferences

class SetupMySqlConfig(myIni: File) {
    private val readMyIni = ReadOptionMyini(myIni).read()
    private val writeMyIni = WriteOptionMyini(myIni)

    private val mySqlStop = "net stop mysql_jhcis"
    private val mySqlStart = "net start mysql_jhcis"

    init {
        if (checkLogNeedConfig()) {
            if (isAdmin()) {
                if (writeConfig()) {
                    stopJhcisMySql()
                    startJhcisMySql()
                } else {
                    throw Exception("Windows check config my.ini please set log = jlog.log")
                }
            } else {
                throw Exception("Windows config my.ini need administrator permission.")
            }
        }
        startJhcisMySql()
    }

    internal fun stopJhcisMySql() {
        val proc = Runtime.getRuntime().exec(mySqlStop)
        while (proc.isAlive) {
            Thread.sleep(200)
        }
    }

    internal fun startJhcisMySql() {
        val proc = Runtime.getRuntime().exec(mySqlStart)
        while (proc.isAlive) {
            Thread.sleep(200)
        }
    }

    internal fun writeConfig(): Boolean {
        if (checkLogNeedConfig()) {
            writeMyIni.writeMysqld("log" to "jlog.log")
            return true
        }
        return false
    }

    private fun checkLogNeedConfig() = readMyIni.getValue("mysqld")["log"] == null

    private fun isAdmin(): Boolean {
        val prefs = Preferences.systemRoot()
        return try {
            prefs.put("foo", "bar") // SecurityException on Windows
            prefs.remove("foo")
            prefs.flush() // BackingStoreException on Linux
            true
        } catch (e: Exception) {
            false
        }
    }
}

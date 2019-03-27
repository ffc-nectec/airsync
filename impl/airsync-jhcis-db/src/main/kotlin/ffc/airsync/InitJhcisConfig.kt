package ffc.airsync

import ffc.airsync.utils.startJhcisMySql
import ffc.airsync.utils.stopJhcisMySql
import mysql.config.read.ReadOptionMyini
import mysql.config.write.WriteOptionMyini
import java.io.File
import java.util.prefs.Preferences

class InitJhcisConfig(myIni: File) {
    private val readMyIni = ReadOptionMyini(myIni).read()
    private val writeMyIni = WriteOptionMyini(myIni)

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

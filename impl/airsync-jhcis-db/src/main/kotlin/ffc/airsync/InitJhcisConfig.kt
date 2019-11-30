package ffc.airsync

import ffc.airsync.utils.startJhcisMySql
import ffc.airsync.utils.stopJhcisMySql
import mysql.config.read.ReadOptionMyini
import mysql.config.write.WriteOptionMyini
import soulbit.version.operator.VersionOp
import java.io.File
import java.util.prefs.Preferences

private const val MySqlNewVersionWhen = "5.6.1"
private const val logFileName = "jlog.log"

class InitJhcisConfig(private val myIni: File, private val dbVersion: String) {
    private val readMyIni = ReadOptionMyini(myIni).read()
    private val writeMyIni = WriteOptionMyini(myIni)

    init {

        if (dbVersion.VersionOp() < MySqlNewVersionWhen.VersionOp())
            oldMySqlConfig()
        else
            newMySqlConfig()
    }

    private fun oldMySqlConfig() {
        if (checkOldMySqlConfig()) {
            if (isAdmin()) {
                if (writeOldMySqlConfig()) {
                    stopJhcisMySql()
                    startJhcisMySql()
                } else {
                    throw Exception("Windows check config my.ini please set log = jlog.log")
                }
            } else {
                throw Exception("Windows config my.ini need administrator permission.")
            }
        }
    }

    private fun newMySqlConfig() {
        if (checkNewMySqlConfig()) {
            if (isAdmin()) {
                if (writeNewMySqlConfig()) {
                    stopJhcisMySql()
                    startJhcisMySql()
                } else {
                    throw Exception("Windows check config my.ini please set log = jlog.log")
                }
            } else {
                throw Exception("Windows config my.ini need administrator permission.")
            }
        }
    }

    internal fun writeOldMySqlConfig(): Boolean {
        if (checkOldMySqlConfig()) {
            writeMyIni.writeMysqld("log" to logFileName)
            return true
        }
        return false
    }

    private fun writeNewMySqlConfig(): Boolean {
        if (checkNewMySqlConfig()) {
            writeMyIni.writeMysqld("general_log_file" to logFileName)
            writeMyIni.writeMysqld("general_log" to "ON")
            return true
        }
        return false
    }

    private fun checkOldMySqlConfig() = readMyIni.getValue("mysqld")["log"] == null
    private fun checkNewMySqlConfig() =
        readMyIni.getValue("mysqld")["general_log"] == null || readMyIni.getValue("mysqld")["general_log"] == null

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

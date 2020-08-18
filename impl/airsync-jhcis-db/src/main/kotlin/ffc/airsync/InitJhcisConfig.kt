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

class InitJhcisConfig(myIni: File, dbVersion: String) {
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
                throw Exception(
                    "โปรดเรียกใช้งาน ffc-airsync-installer.exe ใน administrator mode.\n" +
                            "Windows config my.ini need administrator permission."
                )
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
                throw Exception(
                    "โปรดเรียกใช้งาน ffc-airsync-installer.exe ใน administrator mode.\n" +
                            "Windows config my.ini need administrator permission."
                )
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

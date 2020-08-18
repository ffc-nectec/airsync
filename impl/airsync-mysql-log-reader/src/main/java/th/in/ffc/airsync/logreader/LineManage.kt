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

import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

class LineManage(var logConfig: String = "C:\\Program Files\\JHCIS\\MySQL\\data\\log.cnf") {

    private lateinit var properties: Properties

    init {
        loadProperty()
    }

    fun getProperty(key: String): String {
        loadProperty()
        return properties.getProperty(key, "")
    }

    fun setProperty(key: String, value: String) {
        properties.setProperty(key, value)
        saveProperty()
    }

    fun setLastLineNumber(lineNumber: Long) {
        setProperty("lastline", lineNumber.toString())
    }

    fun getLastLineNumber(): Long {
        val lineNumberStr = getProperty("lastline")
        return if (lineNumberStr != "") {
            lineNumberStr.toLong()
        } else {
            0
        }
    }

    private fun saveProperty() {
        properties.store(FileOutputStream(logConfig), null)
    }

    private fun loadProperty() {
        val conf = Properties()
        try {
            conf.load(FileInputStream(logConfig))
        } catch (ignore: java.io.FileNotFoundException) {
        }
        properties = conf
    }
}

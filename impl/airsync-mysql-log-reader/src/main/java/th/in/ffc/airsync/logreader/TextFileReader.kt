/*
 * Copyright (c) 2018 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package th.`in`.ffc.airsync.logreader

import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException

class TextFileReader @Throws(FileNotFoundException::class)
@JvmOverloads constructor(fileparth: String, realtime: Boolean = true, delay: Long = 2000) {
    private var linenumber: Long = 0
    private lateinit var bufferReader: BufferedReader
    private var listener: (queryRecord: QueryRecord) -> Unit = {}
    private var realtime: Boolean = false
    private var delay: Long = 0

    init {
        run {
            this.realtime = realtime
            this.delay = delay
            val textfilepath = File(fileparth)
            var openLog = false
            // Wait Open Log
            while (!openLog) {
                try {
                    bufferReader = BufferedReader(FileReader(textfilepath))
                    openLog = true
                } catch (ex: java.io.FileNotFoundException) {
                    try {
                        Thread.sleep(3000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun setListener(listener: (queryRecord: QueryRecord) -> Unit) {
        this.listener = listener
    }

    fun stop() {
        realtime = false
    }

    @Throws(IOException::class)
    fun process() {
        var line: String?
        do {
            line = bufferReader.readLine()
            while (line != null) {
                this.listener(QueryRecord(line, linenumber++))
                line = bufferReader.readLine()
            }
            try {
                if (realtime) Thread.sleep(delay)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        } while (realtime)
        bufferReader.close()
    }

    internal interface LogEvent {
        fun process(record: QueryRecord)
    }
}

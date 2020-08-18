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

import org.junit.Ignore

class HowToUse {

    val filter = hashMapOf<String, List<String>>().apply {
        put("house", listOf("house", "`house`"))
    }

    @Ignore("How to use TextFileReader")
    fun exampleTextFileReader() {
        val readTextFile = TextFileReader(Config.logfilepath, true, 100)

        readTextFile.setListener(LineManage("temp.a")) {
            println(it.log)
        }

        readTextFile.process()

        while (true)
            Thread.sleep(3000)
    }

    @Ignore("How to use LogReader")
    fun exampleLogReader() {

        val logReader = LogReader(Config.logfilepath, tablesPattern = filter) { tableName, keyWhere ->
            println("Table:$tableName,Where:$keyWhere")
        }

        logReader.start()
        while (true)
            Thread.sleep(3000)
    }
}

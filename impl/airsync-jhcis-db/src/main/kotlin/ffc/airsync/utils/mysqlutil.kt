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

package ffc.airsync.utils

private val mySqlStop = "net stop mysql_jhcis"
private val mySqlStart = "net start mysql_jhcis"

fun stopJhcisMySql() {
    val proc = Runtime.getRuntime().exec(mySqlStop)
    while (proc.isAlive) {
        Thread.sleep(200)
    }
}

fun startJhcisMySql() {
    val proc = Runtime.getRuntime().exec(mySqlStart)
    while (proc.isAlive) {
        Thread.sleep(200)
    }
}

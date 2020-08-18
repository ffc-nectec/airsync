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

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

private interface CallApi

private val logger by lazy { getLogger(CallApi::class) }

fun <T> callApi(
    msRuntime: (Long) -> Unit = {},
    cleanAll: () -> Unit = {},
    call: () -> T?
): T {

    cleanAll()
    var loop = 0

    while (true) {
        try {
            var result: T? = null
            val runtime = measureTimeMillis {
                call()?.let { result = it }
            }
            msRuntime(runtime)

            return result!!
        } catch (ex: java.net.SocketTimeoutException) {
            if (loop > 5) throw ex
            logger.warn("Time out loop ${++loop}")
            runBlocking { delay(1000) }
        } catch (ex: ApiLoopException) {
            if (loop > 5) throw ex
            logger.warn("Loop ${++loop} api custom by user cannot return standard ${ex.message}")
            runBlocking { delay(1000) }
        } catch (ex: java.net.SocketException) {
            if (loop > 5) throw ex
            logger.warn("Socket error check network ${++loop}")
            runBlocking { delay(1000) }
        } catch (ex: java.net.UnknownHostException) {
            logger.error(ex) { "เน็ตหลุด หรือ เชื่อมต่อกับ server ไม่ได้ delay 10s ${ex.message}" }
            runBlocking { delay(1000) }
        }
    }
}

fun Long.toStringTime(): String {
    if (this > 0) {
        val sec = (this / 1000) % 60
        val min = (this / 60000) % 60
        val hour = (this / 36e5).toInt()
        return ("\t$hour:$min:$sec")
    }
    return ""
}

fun callApiNoReturn(call: () -> Unit) {
    var loop = 1
    while (true) {
        try {
            call()
            return
        } catch (ex: java.net.SocketTimeoutException) {
            if (loop > 5) throw ex
            logger.warn("Time out loop $loop")
            loop++
            runBlocking { delay(1000) }
        } catch (ex: java.net.SocketException) {
            if (loop > 5) throw ex
            logger.warn("Socket error check network $loop")
            loop++
            runBlocking { delay(1000) }
        } catch (ex: ApiLoopException) {
            if (loop > 5) throw ex
            logger.warn("Time out loop $loop")
            loop++
            runBlocking { delay(1000) }
        } catch (ex: java.net.UnknownHostException) {
            logger.error(ex) { "เน็ตหลุด หรือ เชื่อมต่อกับ server ไม่ได้ delay 10s ${ex.message}" }
            Thread.sleep(10000)
        }
    }
}

class ApiLoopException(override val message: String? = null) : Exception(message)

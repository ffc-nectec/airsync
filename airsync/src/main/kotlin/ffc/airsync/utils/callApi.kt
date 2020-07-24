package ffc.airsync.utils

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
            Thread.sleep(10000)
        } catch (ex: ApiLoopException) {
            if (loop > 5) throw ex
            logger.warn("Loop ${++loop} api custom by user cannot return standard ${ex.message}")
            Thread.sleep(10000)
        } catch (ex: java.net.SocketException) {
            if (loop > 5) throw ex
            logger.warn("Socket error check network ${++loop}")
            Thread.sleep(10000)
        } catch (ex: java.net.UnknownHostException) {
            logger.error(ex) { "เน็ตหลุด หรือ เชื่อมต่อกับ server ไม่ได้ delay 10s ${ex.message}" }
            Thread.sleep(10000)
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
        } catch (ex: java.net.SocketException) {
            if (loop > 5) throw ex
            logger.warn("Socket error check network $loop")
            Thread.sleep(10000)
            loop++
        } catch (ex: ApiLoopException) {
            if (loop > 5) throw ex
            logger.warn("Time out loop $loop")
            loop++
        } catch (ex: java.net.UnknownHostException) {
            logger.error(ex) { "เน็ตหลุด หรือ เชื่อมต่อกับ server ไม่ได้ delay 10s ${ex.message}" }
            Thread.sleep(10000)
        }
    }
}

class ApiLoopException(override val message: String? = null) : Exception(message)

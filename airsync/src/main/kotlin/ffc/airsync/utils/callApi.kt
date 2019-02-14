package ffc.airsync.utils

import ffc.airsync.printDebug
import kotlin.system.measureTimeMillis

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
            printDebug("Time out loop ${++loop}")
            ex.printStackTrace()
        } catch (ex: ApiLoopException) {
            printDebug("Loop api custom by user ${ex.message}")
        } catch (ex: java.net.SocketException) {
            printDebug("Socket error check network ${++loop}")
            Thread.sleep(10000)
            ex.printStackTrace()
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
    var loop = 0
    while (true) {
        try {
            call()
            return
        } catch (ex: java.net.SocketTimeoutException) {
            printDebug("Time out loop ${++loop}")
            ex.printStackTrace()
        } catch (ex: java.net.SocketException) {
            printDebug("Socket error check network ${++loop}")
            Thread.sleep(10000)
            ex.printStackTrace()
        }
    }
}

class ApiLoopException(override val message: String? = null) : Exception(message)

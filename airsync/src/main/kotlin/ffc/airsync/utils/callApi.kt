package ffc.airsync.utils

import kotlin.system.measureTimeMillis

fun <T> callApi(cleanAll: () -> Unit = {}, call: () -> T?): T {
    cleanAll()
    var loop = 0

    while (true) {
        try {
            var result: T? = null
            val runtime = measureTimeMillis {
                call()?.let { result = it }
            }

            val sec = (runtime / 1000) % 60
            val min = (runtime / 60000) % 60
            val hour = (runtime / 36e5).toInt()
            print("\tTime:$runtime $hour:$min:$sec")

            return result!!
        } catch (ex: java.net.SocketTimeoutException) {
            println("Time out loop ${++loop}")
            ex.printStackTrace()
        } catch (ex: ApiLoopException) {
            println("Loop api custom by user ${ex.message}")
        }
    }
}

fun callApiNoReturn(call: () -> Unit) {
    var loop = 0
    while (true) {
        try {
            call()
            return
        } catch (ex: java.net.SocketTimeoutException) {
            println("Time out loop ${++loop}")
            ex.printStackTrace()
        }
    }
}

class ApiLoopException(override val message: String? = null) : Exception(message)

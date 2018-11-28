package ffc.airsync.utils

fun <T> callApi(cleanAll: () -> Unit = {}, call: () -> T?): T {
    cleanAll()
    var loop = 0

    while (true) {
        try {
            call()?.let { return it }
        } catch (ex: java.net.SocketTimeoutException) {
            println("Time out loop ${++loop}")
            ex.printStackTrace()
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

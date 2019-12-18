package ffc.airsync.utils

inline fun <reified T> ignore(callback: () -> T?): T? {
    return try {
        callback()
    } catch (ignore: Exception) {
        null
    }
}

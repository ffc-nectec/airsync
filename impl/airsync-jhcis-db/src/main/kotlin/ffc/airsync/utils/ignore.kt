package ffc.airsync.utils

import ffc.airsync.getLogger

inline fun <reified T> ignore(callback: () -> T): T? {
    return try {
        callback()
    } catch (ignore: Exception) {
        getLogger(T::class).warn("Ignore error ${ignore.message}", ignore)
        null
    }
}

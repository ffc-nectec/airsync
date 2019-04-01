package ffc.airsync.utils

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

inline fun <reified T> Any.getLogger(clazz: T): Logger {

    return LogManager.getLogger(T::class.java)
}

fun Any.getLogger(): Logger {

    return LogManager.getLogger(this::class.java)
}

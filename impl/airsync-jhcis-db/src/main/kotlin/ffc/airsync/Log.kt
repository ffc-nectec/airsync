package ffc.airsync

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

inline fun <reified T> getLogger(clazz: T): Logger {
    return LogManager.getLogger(T::class.java)
}

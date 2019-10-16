package ffc.airsync.utils

import org.apache.logging.log4j.kotlin.KotlinLogger
import org.apache.logging.log4j.kotlin.logger

inline fun <reified T> getLogger(clazz: T): KotlinLogger {
    return logger(T::class.java.simpleName)
}

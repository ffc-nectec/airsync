package ffc.airsync

import org.jdbi.v3.core.Jdbi

interface Dao {
    val instant: Jdbi
}

inline fun <reified E, reified R> Dao.extension(crossinline call: E.() -> R): R {
    return instant.extension(call)
}

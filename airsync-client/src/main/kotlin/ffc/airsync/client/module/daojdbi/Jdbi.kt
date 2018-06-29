package ffc.airsync.client.module.daojdbi

import org.jdbi.v3.core.Jdbi
import java.lang.RuntimeException

inline fun <reified E, reified R> Jdbi.extension(crossinline call: E.() -> R): R {
    return withExtension<R, E, RuntimeException>(E::class.java, { call(it) })
}

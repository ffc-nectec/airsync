/*
 * Copyright (c) 2018 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.airsync

import org.jdbi.v3.core.Jdbi

inline fun <reified E, reified R> Jdbi.extension(crossinline call: E.() -> R): R {
    var loop = 0
    val logger by lazy { getLogger(this) }
    while (true) {
        try {
            return withExtension<R, E, RuntimeException>(E::class.java) {
                call(it)
            }
        } catch (ex: org.jdbi.v3.core.ConnectionException) {
            if (loop < 3) {
                logger.error("JDBI Error Loop 1 Except $loop $ex")
            } else throw ex
        } catch (ex: com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException) {
            if (loop < 3) {
                logger.error("JDBI Error Loop 2 Except $loop $ex")
            } else throw ex
        } finally {
            loop++
            Thread.sleep(3000)
        }
    }
}

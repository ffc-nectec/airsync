/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package ffc.airsync.api.sync

internal class SetUtil<T> {

    interface Func<T> {
        /**
         * ข้อมูลเอกลักษณ์ 2 Object คืออันเดียวกัน
         * โดยยังไม่สนใจข้อมูลด้านใน
         */
        val identity: String
    }

    /**
     * @return list identity
     */
    fun intersection(a: List<T>, b: List<T>, func: (main: T) -> Func<T>): List<Pair<T, T>> {

        val preData = run {
            val aSet = a.map {
                func(it).identity
            }.toSet().toSortedSet()

            val bSet = b.map {
                func(it).identity
            }.toSet().toSortedSet()

            aSet intersect bSet
        }.mapNotNull { it }.toSortedSet()

        val aDataSet = createCacheObject(a, func)
        val bDataSet = createCacheObject(b, func)

        return preData.map {
            aDataSet[it]!! to bDataSet[it]!!
        }
    }

    /**
     * สร้างแคชสำหรับการค้นหา
     */
    private fun createCacheObject(
        a: List<T>,
        func: (main: T) -> Func<T>
    ): HashMap<String, T> {
        return {
            val temp = hashMapOf<String, T>()
            a.forEach {
                temp[func(it).identity] = it
            }
            temp
        }.invoke()
    }

    fun difference(a: List<T>, b: List<T>, func: (main: T) -> Func<T>): List<T> {
        val preData = {
            val aSet = a.map {
                func(it).identity
            }.toSet().toSortedSet()

            val bSet = b.map {
                func(it).identity
            }.toSet().toSortedSet()
            aSet subtract bSet
        }.invoke()

        val aDataSet = createCacheObject(a, func)

        return preData.map { aDataSet[it]!! }
    }
}

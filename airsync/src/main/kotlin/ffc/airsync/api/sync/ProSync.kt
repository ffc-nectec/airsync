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

interface ProSync<T> {
    interface UpdateFunc<T> {
        val identity: String

        /**
         * https://en.wikipedia.org/wiki/Unix_time
         */
        val unixTime: Long

        /**
         * อัพเดทไปยัง item
         */
        fun updateTo(item: T)
    }

    fun update(a: List<T>, b: List<T>, forceUpdate: Boolean = false, func: (item: T) -> UpdateFunc<T>)

    interface CreateFunc {
        /**
         * เอกลักษณ์ object
         */
        val identity: String

        /**
         * สถานะ confirm การลบ
         */
        val bIsDelete: Boolean
        fun createInB()
    }

    fun createNewDataInB(a: List<T>, b: List<T>, func: (item: T) -> CreateFunc)

    interface DeleteFunc {
        val identity: String
        val bIsDelete: Boolean
        fun deleteInB()
    }

    /**
     * ดูว่าข้อมูลอะไรที่มีใน a แต่ไม่มีใน b ให้ลบใน b ทิ้ง
     */
    fun deleteDataInB(a: List<T>, b: List<T>, func: (item: T) -> DeleteFunc)
}

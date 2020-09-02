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

import ffc.airsync.api.sync.ProSync.UpdateFunc

/**
 * Process การ sync
 */
class V1ProSync<T> : ProSync<T> {
    private val util = SetUtil<T>()

    override fun update(a: List<T>, b: List<T>, func: (item: T) -> UpdateFunc<T>) {
        val updatePreData = util.intersection(a, b) {
            object : SetUtil.Func<T> {
                override val identity: String = func(it).identity
            }
        }

        updatePreData.forEach { item ->

            if (func(item.first).unixTime > func(item.second).unixTime) {
                func(item.first).updateTo(item.second)
            } else if (func(item.second).unixTime > func(item.first).unixTime) {
                func(item.second).updateTo(item.first)
            }
            // == ไม่ต้องเอาเพราะแปลว่าไม่มีการอัพเดท
        }
    }

    override fun createNewDataInB(a: List<T>, b: List<T>, func: (item: T) -> ProSync.CreateFunc<T>) {
        val createPreData = util.difference(a, b) {
            object : SetUtil.Func<T> {
                override val identity: String = func(it).identity
            }
        }.filter { !func(it).bIsDelete }

        createPreData.forEach {
            func(it).createInB()
        }
    }
}

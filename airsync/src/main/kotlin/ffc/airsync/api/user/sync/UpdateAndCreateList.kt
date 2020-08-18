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

package ffc.airsync.api.user.sync

import ffc.airsync.api.user.sync.UserDataStatus.CREATE
import ffc.airsync.api.user.sync.UserDataStatus.UPDATE
import ffc.entity.User
import ffc.entity.copy
import ffc.entity.update

class UpdateAndCreateList {

    /**
     * ค้นหารายการ รายชื่อ user ที่ update และ user ใหม่ที่ต้อง create
     * จะคิดโดยตั้งรายการแรก แล้วไป ค้นหาในรายการที่สอง
     * โดย user ที่อัพเดท จะแก้ ID ให้ตรงกับ two ให้
     * @param one รายการเแรก Local
     * @param two รายการสอง Cloud
     * @return updateList, createList, all(จับคู่พร้อมแก้ id เป็น one ให้ พร้อมใช้ส่งข้อมูล update)
     */
    fun getList(one: List<User>, two: List<User>): Triple<List<User>, List<User>, List<User>> {
        val mapLocalWithCLoud = mapUserOneWithTwo(one, two)
        val updateList = arrayListOf<User>()
        val createList = arrayListOf<User>()
        val all = arrayListOf<User>()

        mapLocalWithCLoud.forEach { item ->
            // รายการ update
            if (item.third == UPDATE) {
                updateList.add(item.first.copy(item.second!!.id).update(item.first.timestamp) {})
            }
            // รายการสร้างใหม่
            if (item.third == CREATE) {
                createList.add(item.first)
            }
            // รายการจับคู่ได้ทั้งหมด
            if (item.second != null) {
                all.add(item.first.copy(item.second!!.id).update(item.first.timestamp) {})
            }
        }
        return Triple(updateList.toList(), createList.toList(), all.toList())
    }
}

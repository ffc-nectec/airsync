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

package ffc.airsync.api.house

import ffc.airsync.api.Sync
import ffc.entity.place.House

/**
 * ใช้เพื่อให้ airsync เข้าถึงข้อมูล house
 * ปรับจากเดินที่เข้าถึง dao โดยตรง มาเป็นผ่าน Interface
 */
interface HouseInterface : Sync {
    /**
     * เข้าถึงข้อมูลใน Local จากฐาน JHCISDB
     */
    val local: List<House>

    /**
     * เข้าถึงข้อมูลใน Cloud จาก api
     */
    val cloud: List<House>
    fun directUpdateCloudData(list: List<House>)

    /**
     * เวลา auto sync เรียกอัพเดทเฉพาะ Object
     */
    fun sync(id: String)

    /**
     * ลบข้อมูลทั้งหมด ใช้เมื่อต้องการจะ Sync ใหม่เท่านั้น
     */
    fun clear()
}

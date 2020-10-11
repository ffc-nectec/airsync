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

package ffc.airsync.api.person

import ffc.airsync.api.Sync
import ffc.entity.Person
import ffc.entity.Person.Relationship

/**
 * ใช้เพื่อให้ airsync เข้าถึงข้อมูล person
 * ปรับจากเดินที่เข้าถึง dao โดยตรง มาเป็นผ่าน Interface
 */
interface PersonInterface : Sync {
    /**
     * เข้าถึงข้อมูลบน cloud จาก API
     * ภายในอาจมีการทำแคชเอาไว้
     */
    val cloud: List<Person>

    /**
     * เข้าถึงข้อมูลจาก Local ฐาน JHCISDB
     */
    val local: List<Person>

    /**
     * List personId,List relatoin
     */
    fun updateRelation(listUpdateRelation: List<Pair<String, List<Relationship>>>)

    /**
     * คันหา person ในชุดข้อมูล Cloud
     * ปกติใช้เพื่อในการแมพหา id ของ Object
     * เพราะ ข้อมูลบน Cloud จะมี Id ที่สร้างจาก api แต่ใน Local จะเป็น Temp ID
     */
    fun findPersonIdInCloud(pcuCode: String, pid: String): Person?

    /**
     * ลบข้อมูลทั้งหมด ใช้เมื่อต้องการจะ Sync ใหม่เท่านั้น
     */
    fun clear()
}

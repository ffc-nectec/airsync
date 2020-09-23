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

package ffc.airsync.visit

import ffc.airsync.getLogger

/**
 * ช้องกรองจัดการ Lookup ตอน sync visit
 * จะทำเป็นแยกแต่ละครั้งที่ต้องการดึง visit ทั้งหมด
 * -ป้องกันการ sync user ที่ไม่พบซ้ำ
 * -ป้องกันการ sync person ที่ไม่พบซ้ำ
 */
internal class LookupManage(private val func: () -> Lookup) {
    interface Lookup {
        fun patientId(pcuCode: String, pid: String): String?
        fun providerId(username: String): String?
    }

    /**
     * เก็บรายการที่ค้นหา user ไม่พบไว้ เพื่อเวลาค้นหาจะได้ดูจากรายการที่เคยไม่พบก่อน
     * เป็นการลด process ในระบบ แต่อาจจะเพิ่ม Memory
     */
    private val providerMissing = HashSet<String>()

    /**
     * เก็บรายการที่ค้นหา partient หรือ person ไม่พบไว้ เพื่อเวลาค้นหาจะได้ดูจากรายการที่เคยไม่พบก่อน
     * เป็นการลด process ในระบบ แต่อาจจะเพิ่ม Memory
     */
    private val patientMissing = HashSet<String>()

    /**
     * เก็บรายการที่ค้นหาพบไว้ เพื่อที่จะไม่จำเป็นต้องทำการค้นหาซ้ำอีก
     */
    private val cachePatientId = HashMap<String, String>()

    /**
     * เก็บรายการที่ค้นหาพบไว้ เพื่อที่จะไม่จำเป็นต้องทำการค้นหาซ้ำอีก
     */
    private val cacheProviderId = HashMap<String, String>()
    private val logger = getLogger(this)

    fun lookupPatientId(pcuCode: String, pid: String): String? {
        val key = "${pcuCode.trim()}:${pid.trim()}"
        return if (patientMissing.contains(key)) {
            null
        } else {
            when (val find = cachePatientId[key] ?: func().patientId(pcuCode, pid)) {
                null, "" -> {
                    patientMissing.add(key)
                    logger.warn { "ค้นหา person pcucode:pid = $key ไม่พบ" }
                    null
                }
                else -> {
                    if (cachePatientId[key].isNullOrBlank())
                        cachePatientId[key] = find
                    find
                }
            }
        }
    }

    fun lookupProviderId(username: String): String? {
        val key = username.trim()
        return if (providerMissing.contains(key)) {
            null
        } else {
            when (val find = cacheProviderId[key] ?: func().providerId(key)) {
                null, "" -> {
                    providerMissing.add(key)
                    logger.warn { "ค้นหา username $key ไม่พบ" }
                    null
                }
                else -> {
                    if (cacheProviderId[key].isNullOrBlank())
                        cacheProviderId[key] = find
                    find
                }
            }
        }
    }
}

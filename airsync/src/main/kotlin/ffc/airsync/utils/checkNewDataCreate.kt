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

package ffc.airsync.utils

/**
 * ใช้สำหรับช่วยในการสร้างชุดคำสั่ง สำหรับการตรวจสอบการ update ข้อมูลใหม่ขึ้นไป cloud
 * @param jhcisData ข้อมูลที่อยู่บน local ในที่นี้จะเป็นข้อมูลจาก jhcis
 * @param cloudData ข้อมูลที่อยู่บน cloud
 * @param checkItem กฏการตรวจสอบว่า วัตถุ local และ cloud เหมือนกัน
 * @param updateNewData ถ้าเกิดมีข้อมูลใหม่จะ create จะให้ทำอย่างไร
 */
fun <T> checkNewDataCreate(
    jhcisData: List<T>,
    cloudData: List<T>,
    checkItem: (jhcis: T, cloud: T) -> Boolean,
    updateNewData: (newData: List<T>) -> Unit
) {
    val newData = arrayListOf<T>()
    jhcisData.forEach { local ->
        val cloud = cloudData.find { checkItem(local, it) }
        if (cloud == null)
            newData.add(local)
    }

    if (newData.isNotEmpty()) {
        updateNewData(newData)
    }
}

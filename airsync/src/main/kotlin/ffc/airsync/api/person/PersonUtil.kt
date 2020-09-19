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

import ffc.entity.Person

/**
 * ดึงข้อมูล hcode เป็นคีย์ของ รหัสบ้านในฐาน JHCISDB
 */
fun Person.getHcode() = link?.keys?.get("hcode")?.toString()?.trim()

/**
 * ดึงข้อมูล pcucode เป็นคีย์ของ รหัสหน่วยงานในฐาน JHCISDB
 */
fun Person.getPcuCode() = link?.keys?.get("pcucodeperson")?.toString()?.trim()

/**
 * ดึงข้อมูล pid เป็นคีย์ของ รหัสคนในฐาน JHCISDB
 */
fun Person.getPid() = link?.keys?.get("pid")?.toString()?.trim()

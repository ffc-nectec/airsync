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

package ffc.airsync.update

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

/**
 * จัดการเกี่ยวกับการตรวจสอบว่า
 * สมคร re-sync หรือไม่
 */
internal class reSyncProperties(
    private val propertyFile: File
) {
    private val prop = Properties()

    init {
        if (!propertyFile.isFile) propertyFile.createNewFile()
        require(propertyFile.isFile) { "ต้องเป็นไฟล์" }
        prop.load(FileInputStream(propertyFile))
    }

    fun gerVersion(): String {
        return prop.getProperty("version") ?: "1"
    }

    /**
     * หลัง sync ทั้งหมดแล้วต้องทำการ set current version ใหม่ใส่เข้าไป
     */
    fun setVersion(version: String) {
        prop["version"] = version
        prop.store(FileOutputStream(propertyFile), null)
    }
}

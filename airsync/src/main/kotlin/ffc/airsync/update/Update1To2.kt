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

import ffc.airsync.Main
import ffc.airsync.healthCare
import ffc.airsync.houseManage
import ffc.airsync.personManage
import ffc.airsync.userManage
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.syncCloud
import ffc.airsync.villages

internal class Update1To2 {
    private val logger = getLogger(this)
    fun runUpdate() {
        logger.info { "Run update." }
        if (userManage.cloudUser.isNotEmpty()) {
            logger.info { "User is not empty. แสดงว่าเคย sync มาแล้ว" }
            logger.info { "ใหัอัพเดทข้อมูล user" }
            userManage.sync(true)
            logger.info { "Sync ข้อมูลที่ค้างอยู่บน cloud ลงสู่ jhcisdb" }
            syncCloud.sync(Main.instant.dao)
        }
        logger.info { "HealthCare clear" }
        healthCare.clear()
        logger.info { "Village clear" }
        villages.clear()
        logger.info { "House clear" }
        houseManage.clear()
        logger.info { "Person clear" }
        personManage.clear()
        logger.info { "Delete Village.json" }
        DeleteFileInData("Village.json").delete()
        logger.info { "Delete healthTemp.json" }
        DeleteFileInData("healthTemp.json").delete()
        logger.info { "Delete HealthCareService.json" }
        DeleteFileInData("HealthCareService.json").delete()
        logger.info { "Delete analyzer.json" }
        DeleteFileInData("analyzer.json").delete()
        logger.info { "Delete relation.json" }
        DeleteFileInData("relation.json").delete()
    }
}

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

package ffc.airsync

import ffc.airsync.api.analyzer.initSync
import ffc.airsync.api.genogram.initRelation
import ffc.airsync.api.healthcare.initSync
import ffc.airsync.api.template.TemplateInit
import ffc.airsync.api.village.initSync
import ffc.airsync.gui.ProgressList
import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.createProgress
import ffc.airsync.utils.getLogger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class InitSync : ProgressList {

    var progressTemplate = 0
    var progressUser = 0
    var progressVillage = 0
    var progressHouse = 0
    var progressPerson = 0
    var progressRelation = 0
    var progressHealthCare = 0
    var progressAnalyzer = 0
    private val logger by lazy { getLogger(this) }

    val progressOrg: Int
        get() {
            // Max 800
            return progressTemplate +
                    progressUser +
                    progressVillage +
                    progressHouse +
                    progressPerson +
                    progressRelation +
                    progressHealthCare +
                    progressAnalyzer
        }
    var message = "เตรียมพร้อม.."

    fun init(gui: AirSyncGUI) {
        var isFinish = false
        GlobalScope.launch {
            while (!isFinish) {
                gui.createProgress("Sync", progressOrg, 800, message)
                delay(500)
            }
            gui.remove("Sync")
        }
        runBlocking {
            launch {
                progressTemplate = 10
                logger.info { "ใส่ข้อมูล ช่วยกรอกอัตโนมัติ...." }
                TemplateInit()
                progressTemplate = 100
            }
            launch {
                logger.info { "ใส่ข้อมูลผู้ใช้" }
                userManage.sync()
                progressUser = 100
            }

            launch {
                logger.info { "เข้าถึงหมู่บ้าน" }
                villages.initSync()
                progressVillage = 100
            }
        }

        logger.info { "ดูบ้าน (3/7)" }
        message = "สำรวจบ้าน "
        houseManage.sync()
        logger.info { "ดูข้อมูลคน (4/7)" }
        message = "สำรวจคน"
        personManage.sync()
        logger.info { "วิเคราะห์ความสัมพันธ์ (5/7)" }
        message = "คำนวณความสัมพันธ์"
        relation.initRelation {
            progressRelation = it
        }
        logger.info { "รวบรวมข้อมูลการให้บริการ 3 ปี... (6/7)" }
        message = "วิเคราะห์การให้บริการ"
        healthCare.initSync {
            progressHealthCare = it
        }
        logger.info("สำรวจความเจ็บป่วย (7/7)")
        message = "วิเคราะห์ความเจ็บป่วย"
        analyzer.initSync(healthCare) {
            progressAnalyzer = it
        }
        logger.info { "Finished push. Sync ข้อมูลสำเร็จ" }
        isFinish = true
    }

    override fun get(): Map<String, Int> {
        val map = hashMapOf<String, Int>()
        map["ระบบช่วยกรอก"] = progressTemplate
        map["ข้อมูลผู้ใช้"] = progressUser
        map["หมู่บ้าน"] = progressVillage
        map["เพิ่มบ้านในระบบ"] = progressHouse
        map["เพิ่มคนในระบบ"] = progressPerson
        map["วิเคราะห์ความสัมพันธ์"] = progressRelation
        map["ข้อมูลการให้บริการ"] = progressHealthCare
        map["สำรวจความเจ็บป่วย"] = progressAnalyzer

        return map.toMap()
    }
}

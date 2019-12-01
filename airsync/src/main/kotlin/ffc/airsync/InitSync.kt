package ffc.airsync

import ffc.airsync.api.analyzer.initSync
import ffc.airsync.api.genogram.initRelation
import ffc.airsync.api.healthcare.initSync
import ffc.airsync.api.house.initSync
import ffc.airsync.api.person.SyncPerson
import ffc.airsync.api.person.initSync
import ffc.airsync.api.template.TemplateInit
import ffc.airsync.api.user.initSync
import ffc.airsync.api.village.initSync
import ffc.airsync.gui.ProgressList
import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.createProgress
import ffc.airsync.utils.getLogger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        progressTemplate = 10
        logger.info { "ใส่ข้อมูล ช่วยกรอกอัตโนมัติ...." }
        TemplateInit()
        progressTemplate = 100

        logger.info { "ใส่ข้อมูลผู้ใช้ (1/7)" }
        users.initSync()
        progressUser = 100

        logger.info { "เข้าถึงหมู่บ้าน (2/7)" }
        villages.initSync()
        progressVillage = 100

        logger.info { "ดูบ้าน (3/7)" }
        message = "สำรวจบ้าน "

        val syncPerson = SyncPerson()
        val jhcisDbPerson = syncPerson.prePersonProcess()
        houses.initSync(jhcisDbPerson) {
            progressHouse = it
        }
        logger.info { "ดูข้อมูลคน (4/7)" }
        message = "สำรวจคน"
        persons.initSync(houses, jhcisDbPerson) {
            progressPerson = it
        }
        logger.info { "วิเคราะห์ความสัมพันธ์ (5/7)" }
        message = "คำนวณความสัมพันธ์"
        relation.initRelation {
            progressRelation = it
        }
        logger.info { "รวบรวมข้อมูลการให้บริการ 1 ปี... (6/7)" }
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

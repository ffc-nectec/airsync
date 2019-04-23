/*
 * Copyright (c) 2018 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.airsync

import ffc.airsync.api.organization.LocalOrganization
import ffc.airsync.db.DatabaseDao
import ffc.airsync.provider.airSyncUiModule
import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.createMessage
import ffc.airsync.ui.createProgress
import ffc.airsync.ui.delayRemove
import ffc.airsync.utils.getDataStore
import ffc.airsync.utils.toBuddistString
import ffc.entity.Organization
import ffc.entity.Token
import org.joda.time.DateTime

class MainController(val dao: DatabaseDao) {

    private val property: LocalOrganization
    var everLogin: Boolean = false

    init {
        gui.createProgress("Database", 3, 10, "กำลังเชื่อมต่อ...")
        property = LocalOrganization(dao, getDataStore("ffcProperty.cnf"))
        gui.createProgress("Database", 10, 10, "เชื่อมต่อสำเร็จ")
        gui.delayRemove("Database", 1000)
    }

    fun run() {
        gui.showWIndows()
        gui.createProgress("Check", 15, 100, "Get organization config.")
        val orgLocal = property.organization
        gui.createProgress("Check", 35, 100, "Validate config.")
        checkProperty(orgLocal)
        gui.createProgress("Check", 75, 100, "Validate cloud.")
        try {
            registerOrg(orgLocal)
        } catch (ex: java.lang.Exception) {
            gui.createMessage(
                "Organization Error", "ตรวจสอบพบการลงทะเบียนซ้ำ อาจเกิดจากการลบและติดตั้งใหม่ โปรดติดต่อผู้ดูแล FFC",
                AirSyncGUI.MESSAGE_TYPE.ERROR
            )

            throw ex
        }
        gui.createProgress("Check", 100, 100, "Validate cloud.")
        gui.delayRemove("Check", 1000)
        InitSync().init(gui)
        gui.createProgress("Setup", 1, 4, " Auto sync..")
        SetupAutoSync(dao)
        gui.createProgress("Setup", 2, 4, " Notification.")
        SetupNotification(dao)
        gui.createProgress("Setup", 3, 4, " Database watcher.")
        SetupDatabaseWatcher(dao)
        gui.createProgress("Setup", 4, 4, " Sync..")
        gui.remove("Setup")
        gui.createMessage("Success", "ข้อมูล Sync เข้าระบบสำเร็จแล้ว\r\nล่าสุด ${DateTime.now().toBuddistString()}")
        gui.enableSyncButton = true
        gui.callGetOtp = { otpApi.get() }
        gui.enableOtp = true
        startLocalAirSyncServer()
    }

    private fun checkProperty(org: Organization) {
        val token = property.token
        if (token.isNotEmpty()) {
            everLogin = true
            val user = property.userOrg
            org.users.add(user)
            org.bundle["token"] = Token(user, property.token)
        }
    }

    private fun registerOrg(orgPropertyStore: Organization) {
        orgApi.registerOrganization(orgPropertyStore) { organization, token ->
            property.token = token.token
            property.orgId = organization.id
            property.userOrg = organization.users[0]
        }
    }

    private fun startLocalAirSyncServer() {
        airSyncUiModule().start()
    }
}

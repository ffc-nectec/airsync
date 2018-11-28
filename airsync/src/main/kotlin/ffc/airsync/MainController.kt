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

import ffc.airsync.api.analyzer.initSync
import ffc.airsync.api.chronic.Chronics
import ffc.airsync.api.genogram.initRelation
import ffc.airsync.api.healthcare.initSync
import ffc.airsync.api.house.initSync
import ffc.airsync.api.organization.LocalOrganization
import ffc.airsync.api.person.gets
import ffc.airsync.api.person.initSync
import ffc.airsync.api.person.mapChronic
import ffc.airsync.api.user.initSync
import ffc.airsync.db.DatabaseDao
import ffc.airsync.provider.airSyncUiModule
import ffc.airsync.utils.printDebug
import ffc.entity.Organization
import ffc.entity.Person
import ffc.entity.Token

class MainController(val dao: DatabaseDao) {

    private var property = LocalOrganization(dao, "ffcProperty.cnf")
    var everLogin: Boolean = false

    fun run() {
        val orgLocal = property.organization
        checkProperty(orgLocal)
        registerOrg(orgLocal)
        initSync()
        SetupAutoSync(dao)
        SetupNotification(dao)
        SetupDatabaseWatcher(dao)
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

    private fun initSync() {
        val person = Person().gets()
        person.mapChronic(Chronics())

        users.initSync()
        houses.initSync(person)
        persons.initSync(houses, person)
        relation.initRelation()
        healthCare.initSync()
        analyzer.initSync(healthCare)

        printDebug("Finish push")
    }

    private fun startLocalAirSyncServer() {
        airSyncUiModule().start()
    }
}

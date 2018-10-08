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

import ffc.airsync.api.chronic.gets
import ffc.airsync.api.house.chronicCalculate
import ffc.airsync.api.house.gets
import ffc.airsync.api.organization.LocalOrganization
import ffc.airsync.api.person.gets
import ffc.airsync.api.person.mapChronic
import ffc.airsync.api.user.gets
import ffc.airsync.db.DatabaseDao
import ffc.airsync.provider.airSyncUiModule
import ffc.airsync.utils.load
import ffc.airsync.utils.printDebug
import ffc.airsync.utils.save
import ffc.entity.House
import ffc.entity.Organization
import ffc.entity.Person
import ffc.entity.Token
import ffc.entity.User
import ffc.entity.healthcare.Chronic
import ffc.entity.healthcare.Disease

class MainController(val dao: DatabaseDao) {

    private var property = LocalOrganization(dao, "ffcProperty.cnf")
    var everLogin: Boolean = false

    fun run() {
        val orgLocal = property.organization
        checkProperty(orgLocal)
        registerOrg(orgLocal)

        pushData()

        SetupNotification(dao)

        SetupDatabaseWatcher(dao)

        startLocalAirSyncServer()
    }

    private fun registerOrg(orgPropertyStore: Organization) {
        orgApi.registerOrganization(orgPropertyStore) { organization, token ->
            property.token = token.token
            property.orgId = organization.id
            property.userOrg = organization.users[0]
        }
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

    private fun pushData() {
        val localUser = arrayListOf<User>().apply {
            addAll(load())
        }

        val localPersons = arrayListOf<Person>().apply {
            addAll(load())
        }

        val localHouses = arrayListOf<House>().apply {
            addAll(load())
        }

        if (localUser.isEmpty()) {
            localUser.addAll(User().gets())
            users.addAll(userApi.putUser(localUser.toMutableList()))
            users.save()
        } else {
            users.addAll(localUser)
        }

        if (localHouses.isEmpty()) {
            val house = House().gets()

            localHouses.addAll(house)

            house.chronicCalculate(Person().gets())

            houses.addAll(houseApi.putHouse(localHouses))
            houses.save()
        } else {
            houses.addAll(localHouses)
        }

        if (localPersons.isEmpty()) {
            val personFromDb = Person().gets()
            val chronic = Chronic(Disease("", "", "")).gets()

            personFromDb.mapChronic(chronic)

            localPersons.addAll(personFromDb)
            persons.addAll(personApi.putPerson(localPersons))
            persons.save()
        } else {
            persons.addAll(localPersons)
        }
        printDebug("Finish push")
    }

    private fun startLocalAirSyncServer() {
        airSyncUiModule().start()
    }
}

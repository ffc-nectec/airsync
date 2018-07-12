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

import ffc.airsync.api.Api
import ffc.airsync.api.ApiV1
import ffc.airsync.db.DatabaseDao
import ffc.airsync.provider.airSyncUiModule
import ffc.airsync.provider.notificationModule
import ffc.airsync.utils.printDebug
import ffc.entity.Chronic
import ffc.entity.Link
import ffc.entity.Organization
import ffc.entity.Person
import ffc.entity.System
import ffc.entity.User
import ffc.entity.update
import java.util.UUID

class MainController(val dao: DatabaseDao) {

    val api: Api by lazy { ApiV1() }
    lateinit var org: Organization

    fun run() {

        initOrganization()
        val org = api.registerOrganization(org, Config.baseUrlRest)

        pushData(org)
        setupNotificationHandlerFor(org)
        startLocalAirSyncServer()
    }

    private fun initOrganization() {
        org = Organization()
        with(org) {
            val detail = dao.getDetail()
            val hosId = detail["offid"] ?: ""

            name = detail["name"] ?: ""
            link = Link(System.JHICS).apply {
                keys["offid"] = hosId
            }
            users.add(createAirSyncUser(hosId))
            update { }
        }
    }

    private fun pushData(org: Organization) {
        val userList = dao.getUsers().toMutableList()

        val personOrgList = dao.getPerson()
        val chronicList = dao.getChronic()
        val houseList = dao.getHouse()

        val personHaveChronic = personOrgList.mapChronics(chronicList)

        api.putUser(userList, org)
        api.putHouse(houseList, org)
        // api.putPerson(personHaveChronic, org)

        printDebug("Finish push")
    }

    private fun setupNotificationHandlerFor(org: Organization) {
        notificationModule().apply {
            onTokenChange { firebaseToken ->
                api.putFirebaseToken(firebaseToken, org)
            }
            onReceiveDataUpdate { type, id ->
                when (type) {
                    "House" -> api.getHouseAndUpdate(org, id, dao)
                    else -> println("Not type house.")
                }
            }
        }
    }

    private fun startLocalAirSyncServer() {
        airSyncUiModule().start()
    }

    fun List<Person>.mapChronics(chronics: List<Chronic>): List<Person> {
        forEach { person ->
            person.chronics.addAll(chronics.filter {
                it.link!!.keys["pid"] == person.link!!.keys["pid"]
            })
        }
        return this
    }

    fun createAirSyncUser(hosId: String): User = User().update {
        name = "airsync$hosId"
        password = UUID.randomUUID().toString().replace("-", "")
        role = User.Role.ORG
    }
}

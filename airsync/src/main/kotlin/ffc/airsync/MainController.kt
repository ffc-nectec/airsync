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
import ffc.airsync.provider.databaseWatcher
import ffc.airsync.provider.notificationModule
import ffc.airsync.utils.PropertyStore
import ffc.airsync.utils.gets
import ffc.airsync.utils.load
import ffc.airsync.utils.printDebug
import ffc.airsync.utils.save
import ffc.entity.House
import ffc.entity.Link
import ffc.entity.Organization
import ffc.entity.Person
import ffc.entity.System
import ffc.entity.Token
import ffc.entity.User
import ffc.entity.update
import java.util.UUID

class MainController(val dao: DatabaseDao) {

    lateinit var org: Organization
    val houses = arrayListOf<House>()
    val persons = arrayListOf<Person>()
    val users = arrayListOf<User>()
    val pcucode = StringBuilder()
    private var property = PropertyStore("ffcProperty.cnf")
    var everLogin: Boolean = false
    val api: Api by lazy { ApiV1(persons, houses, users, pcucode) }

    fun run() {
        initOrganization(property.orgId)
        val org = checkProperty()
        pushData(org)
        setupNotificationHandlerFor(org)
        databaseWatcher(org)
        startLocalAirSyncServer()
    }

    private fun checkProperty(): Organization {
        val token = property.token
        if (token.isNotEmpty()) {
            everLogin = true
            val user = property.userOrg
            org.users.add(user)
            org.bundle["token"] = Token(user, property.token)
        }

        val org = api.registerOrganization(org, Config.baseUrlRest)
        property.token = (org.bundle.get("token") as Token).token
        property.orgId = org.id
        property.userOrg = org.users[0]
        return org
    }

    private fun databaseWatcher(org: Organization) {
        databaseWatcher(
            Config.logfilepath
        ) { tableName, keyWhere ->
            printDebug("Database watcher $tableName $keyWhere")
            if (tableName == "house") {
                val house = dao.getHouse(keyWhere)
                house.forEach {
                    try {
                        val houseSync = findHouseWithKey(it)
                        houseSync.update(it.timestamp) {
                            road = it.road
                            no = it.no
                            location = it.location
                            link!!.isSynced = true
                        }

                        api.syncHouseToCloud(houseSync)
                    } catch (ignore: NullPointerException) {
                    }
                }
            }
        }.start()
    }

    private fun initOrganization(orgId: String) {
        if (orgId.isNotEmpty()) {
            org = Organization(orgId)
        } else {
            org = Organization()
        }
        with(org) {
            val detail = dao.getDetail()
            val hosId = detail["offid"] ?: ""

            pcucode.append(hosId)

            name = detail["name"] ?: ""
            tel = detail["tel"]
            address = detail["province"]
            link = Link(System.JHICS).apply {
                keys["offid"] = hosId
            }
            users.add(createAirSyncUser(hosId))
            update { }
        }
    }

    private fun pushData(org: Organization) {
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
            users.addAll(api.putUser(localUser.toMutableList()))
            users.save()
        } else {
            users.addAll(localUser)
        }

        if (localPersons.isEmpty()) {
            localPersons.addAll(Person().gets())
            persons.addAll(api.putPerson(localPersons))
            persons.save()
        } else {
            persons.addAll(localPersons)
        }

        if (localHouses.isEmpty()) {
            localHouses.addAll(House().gets())
            houses.addAll(api.putHouse(localHouses))
            houses.save()
        } else {
            houses.addAll(localHouses)
        }

        printDebug("Finish push")
    }

    private fun findHouseWithKey(house: House): House {
        val houseFind = houses.find {
            house.link!!.keys["pcucode"] == it.link!!.keys["pcucode"] &&
                    house.link!!.keys["hcode"] == it.link!!.keys["hcode"]
        }

        return houseFind ?: throw NullPointerException("ค้นหาไม่พบบ้าน")
    }

    private fun setupNotificationHandlerFor(org: Organization) {
        notificationModule().apply {
            onTokenChange { firebaseToken ->
                api.putFirebaseToken(firebaseToken)
            }
            onReceiveDataUpdate { type, id ->
                when (type) {
                    "House" -> api.syncHouseFromCloud(id, dao)
                    "HealthCare" -> api.syncHealthCareFromCloud(id, dao)
                    else -> println("Not type house.")
                }
            }
        }
    }

    private fun startLocalAirSyncServer() {
        airSyncUiModule().start()
    }

    private fun createAirSyncUser(hosId: String): User = User().update {
        name = "airsync$hosId"
        password = UUID.randomUUID().toString().replace("-", "")
        role = User.Role.ORG
    }
}

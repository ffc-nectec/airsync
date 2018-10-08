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

import ffc.airsync.db.DatabaseDao
import ffc.airsync.provider.airSyncUiModule
import ffc.airsync.provider.databaseWatcher
import ffc.airsync.provider.notificationModule
import ffc.airsync.utils.PropertyStore
import ffc.airsync.utils.gets
import ffc.airsync.utils.houses
import ffc.airsync.utils.load
import ffc.airsync.utils.pcucode
import ffc.airsync.utils.persons
import ffc.airsync.utils.printDebug
import ffc.airsync.utils.save
import ffc.airsync.utils.users
import ffc.entity.House
import ffc.entity.Link
import ffc.entity.Organization
import ffc.entity.Person
import ffc.entity.System
import ffc.entity.Token
import ffc.entity.User
import ffc.entity.healthcare.Chronic
import ffc.entity.healthcare.Disease
import ffc.entity.update
import java.util.UUID

class MainController(val dao: DatabaseDao) {

    // lateinit var org: Organization
    private var property = PropertyStore("ffcProperty.cnf")
    var everLogin: Boolean = false

    fun run() {
        val orgPropertyStore = loadProperty(property.orgId)
        checkProperty(orgPropertyStore)
        registerOrg(orgPropertyStore)

        pushData()

        setupNotificationHandlerFor()

        databaseWatcher()

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

    private fun databaseWatcher() {
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

                        houseApi.syncHouseToCloud(houseSync)
                    } catch (ignore: NullPointerException) {
                    }
                }
            }
        }.start()
    }

    private fun loadProperty(orgId: String): Organization {
        val org: Organization
        if (orgId.isNotEmpty()) {
            org = Organization(orgId)
        } else {
            org = Organization()
        }
        with(org) {
            val detail = dao.getDetail()
            val hosId = detail["pcucode"] ?: ""

            pcucode.append(hosId)

            name = detail["name"] ?: ""
            tel = detail["tel"]
            address = detail["province"]
            link = Link(System.JHICS).apply {
                keys["pcucode"] = hosId
            }
            users.add(createAirSyncUser(hosId))
            update { }
        }
        return org
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

            house.forEach {
                val hcode = it.link!!.keys["hcode"] as String
                val persons = Person().gets()

                if (hcode.isNotEmpty() && hcode != "1") {
                    val person = findPersonInHouse(persons, hcode)

                    val personChronic = person.find {
                        it.haveChronic
                    }
                    if (personChronic != null)
                        it.haveChronic = true
                }
            }

            houses.addAll(houseApi.putHouse(localHouses))
            houses.save()
        } else {
            houses.addAll(localHouses)
        }

        if (localPersons.isEmpty()) {
            val personFromDb = Person().gets()
            val chronic = Chronic(Disease("", "", "")).gets()

            mapChronicToPerson(personFromDb, chronic)

            localPersons.addAll(personFromDb)
            persons.addAll(personApi.putPerson(localPersons))
            persons.save()
        } else {
            persons.addAll(localPersons)
        }
        printDebug("Finish push")
    }

    private fun findPersonInHouse(person: List<Person>, hcode: String): List<Person> {
        return person.filter {
            (it.link!!.keys["hcode"] as String).trim() == hcode
        }
    }

    private fun mapChronicToPerson(
        personFromDb: List<Person>,
        chronic: List<Chronic>
    ) {
        personFromDb.forEach {
            if (it.link == null) false

            val personPid = it.link!!.keys["pid"] as String
            if (personPid.isBlank()) false

            val chronicPerson = chronic.filter {
                if (it.link == null) false

                val chronicPid = it.link!!.keys["pid"] as String
                if (chronicPid.isBlank()) false

                (chronicPid == personPid)
            }

            if (chronicPerson.isEmpty()) false

            it.chronics.addAll(chronicPerson)
        }
    }

    private fun findHouseWithKey(house: House): House {
        val houseFind = houses.find {
            house.link!!.keys["pcucode"] == it.link!!.keys["pcucode"] &&
                    house.link!!.keys["hcode"] == it.link!!.keys["hcode"]
        }

        return houseFind ?: throw NullPointerException("ค้นหาไม่พบบ้าน")
    }

    private fun setupNotificationHandlerFor() {
        notificationModule().apply {
            onTokenChange { firebaseToken ->
                notificationApi.putFirebaseToken(firebaseToken)
            }
            onReceiveDataUpdate { type, id ->
                when (type) {
                    "House" -> houseApi.syncHouseFromCloud(id, dao)
                    "HealthCare" -> healthCareApi.syncHealthCareFromCloud(id, dao)
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

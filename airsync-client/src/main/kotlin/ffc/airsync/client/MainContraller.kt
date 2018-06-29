/*
 * Copyright (c) 2561 NECTEC
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

package ffc.airsync.client

import com.google.firebase.auth.FirebaseToken
import ffc.airsync.client.log.printDebug
import ffc.airsync.client.module.ApiFactory
import ffc.airsync.client.module.daojdbi.DatabaseDao
import ffc.airsync.client.webservice.FFCApiClient
import ffc.airsync.client.webservice.module.FirebaseMessage
import ffc.entity.Chronic
import ffc.entity.Organization
import ffc.entity.Person
import ffc.entity.User

class MainContraller(val org: Organization, val databaseDao: DatabaseDao) {

    fun run() {
        val org = messageCentral.registerOrganization(org, Config.baseUrlRest)
        pushData(org)
        setupNotificationHandlerFor(org)
        startLocalAirSyncServer()
    }

    private fun pushData(org: Organization) {
        val userList = ApiFactory().buildUserDao().findAll()
        printDebug("Add put username org = " + org.link!!.keys["pcucode"])
        messageCentral.putUser(userList, org)

        //put house
        val houseList = databaseDao.getHouse()
        messageCentral.putHouse(houseList, org)

        //put person
        val personOrgList = databaseDao.getPerson()
        //messageCentral.putPerson(personOrgList, org)

        //put chronic
        val chronicList = databaseDao.getChronic()
        //messageCentral.putChronic(chronicList, org)

        val personHaveChronic = insertChronic(personOrgList, chronicList)
        messageCentral.putPerson(personHaveChronic, org)

        printDebug("Finish push")
    }

    private fun setupNotificationHandlerFor(org: Organization) {
        FirebaseMessage.instant.onUpdateListener = object : FirebaseMessage.OnUpdateListener {
            override fun onUpdate(token: FirebaseToken) {
                printDebug("OnUpdateListener $token")
                messageCentral.putFirebaseToken(token, org)

            }
        }
        FirebaseMessage.instant.onUpdateHouseListener = object : FirebaseMessage.OnUpdateHouseListener {
            override fun onUpdate(_id: String) {
                printDebug("OnUpdateHouseListener _id $_id")
                messageCentral.getHouseAndUpdate(org = org, _id = _id, databaseDao = databaseDao)

            }
        }
    }

    private fun startLocalAirSyncServer() {
        val ffcApiClient = FFCApiClient("127.0.0.1", 8081)
        ffcApiClient.start()
        ffcApiClient.join()
    }

    private fun insertChronic(listPerson: List<Person>, listChronic: List<Chronic>): List<Person> {
        listPerson.forEach { person ->
            val chronicList = listChronic.filter { it.link!!.keys["pid"] == person.link!!.keys["pid"] }
            chronicList.forEach { person.chronics.add(it) }
        }
        return listPerson
    }

    companion object {
        val messageCentral: CentralMessageManage = CentralMessageMaorgUpdatenageV1()
    }

    fun createAirSyncUser(): User = User().update {
        name = "airsync.jhcis.pcu${org.id}"
    }
}

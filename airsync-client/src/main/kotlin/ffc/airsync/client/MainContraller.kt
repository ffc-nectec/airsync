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

import ffc.airsync.client.module.ApiFactory
import ffc.airsync.client.module.daojdbi.DatabaseDao
import ffc.airsync.client.module.daojdbi.JdbiDatabaseDao
import ffc.model.*
import java.util.*
import ffc.airsync.client.webservice.FFCApiClient
import ffc.airsync.client.webservice.module.FirebaseMessage
import kotlin.collections.ArrayList


class MainContraller {

    companion object {
        val messageCentral: CentralMessageManage = CentralMessageMaorgUpdatenageV1()
    }

    fun main(dbHost: String, dbPort: String, dbName: String, dbUsername: String, dbPassword: String, orgUuid: String, orgName: String, orgCode: String) {


        //get config
        //check my.ini
        //check log resume
        //check database connection


        //register central
        //messageCentral = CentralMessageMaorgUpdatenageV1()

        var org = Organization(uuid = UUID.fromString(orgUuid), id = "-1", pcuCode = orgCode, name = orgName)
        org = messageCentral.registerOrganization(org, Config.baseUrlRest)

        //put user
        val userList = ApiFactory().buildUserDao().findAll()
        printDebug("Add put username org = " + org.token)
        messageCentral.putUser(userList, org)


        //Create connect database
        val databaseDao: DatabaseDao = JdbiDatabaseDao(
          dbHost = dbHost,
          dbPort = dbPort,
          dbName = dbName,
          dbUsername = dbUsername,
          dbPassword = dbPassword)

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

        val ffcApiClient = FFCApiClient("127.0.0.1", 8081)
        ffcApiClient.start()
        ffcApiClient.join()


    }


    private fun insertChronic(listPerson: List<Person>, listChronic: List<Chronic>): List<Person> {
        val listPersonChronic = arrayListOf<Person>()
        listPerson.forEach {
            val person = it
            val chronicList = listChronic.filter { it.pid == person.pid }
            person.chronics = arrayListOf()
            chronicList.forEach {
                person.chronics.add(it)
            }
        }

        return listPersonChronic
    }
}

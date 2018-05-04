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
import com.google.firebase.iid.FirebaseInstanceId
import ffc.airsync.client.webservice.FFCApiClient


class MainContraller {


    fun main(dbHost: String, dbPort: String, dbName: String, dbUsername: String, dbPassword: String, orgUuid: String, orgName: String, orgCode: String) {


        val ffcApiClient = FFCApiClient("127.0.0.1", 8081)
        ffcApiClient.start()
        ffcApiClient.join()

        //get config
        //check my.ini
        //check log resume
        //check database connection


        //register central
        val messageCentral: CentralMessageManage = CentralMessageMaorgUpdatenageV1()

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
        messageCentral.putPerson(personOrgList, org)

        //put chronic
        val chronicList = databaseDao.getChronic()
        messageCentral.putChronic(chronicList, org)


        /* val socket = PcuSocketAuthByToken(object : PcuSocket.OnEventCallbackMessageListener {
             override fun EventCallBackMessage(message: String) {
                 if (message == "X") {
                    // messageCentral.getData()

                 } else {// Cannot X

                 }
             }

         },org)




         socket.connect(URI.create(org.socketUrl))
         socket.join()
         */
        printDebug("Finish push")
        while (true) {
            var actionList: List<ActionHouse>? = null
            try {
                actionList = messageCentral.syncAction(org)

                actionList.forEach {

                    try {
                        databaseDao.upateHouse(it.action)
                        messageCentral.syncActionUpdateStatus(org, it.actionId, ActionHouse.STATUS.COMPLETE)
                    } catch (ex: Exception) {

                    }
                }


            } catch (ex: NullPointerException) {

            }




            Thread.sleep(5000)
        }

    }
}

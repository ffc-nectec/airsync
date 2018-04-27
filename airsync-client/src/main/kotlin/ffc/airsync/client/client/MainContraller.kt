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

package ffc.airsync.client.client

import ffc.airsync.client.client.module.ApiFactory
import ffc.airsync.client.client.module.PcuSocket
import ffc.airsync.client.client.module.PcuSocketAuthByToken
import ffc.airsync.client.client.module.daojdbi.DatabaseDao
import ffc.airsync.client.client.module.daojdbi.JdbiDatabaseDao
import ffc.model.*
import java.net.URI
import java.util.*
import javax.swing.Action

class MainContraller {


    fun main(dbHost: String, dbPort: String, dbName: String, dbUsername: String, dbPassword: String, orgUuid: String, orgName: String, orgCode: String) {
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
                printDebug("Have action")
                actionList = messageCentral.getAction(org)
                actionList.forEach {
                    printDebug(it)
                }
            } catch (ex: NullPointerException) {
                printDebug("Not fornd action")
            }


            Thread.sleep(5000)
        }

    }
}

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
import javax.ws.rs.NotFoundException


class CentralMessageMaorgUpdatenageV1 : CentralMessageManage {


    val restService = ApiFactory().buildApiClient(Config.baseUrlRest)

    companion object {
        var organization: Organization? = null
        var urlBase: String? = null
    }


    override fun syncAction(org: Organization): List<ActionHouse> {
        val data = restService!!.syncHouseAction(orgId = org.id, authkey = "Bearer " + org.token!!).execute()

        if (data.code() == 200) {
            val syncData = data.body()!!

            return syncData


        } else if (data.code() == 404) {
            printDebug("Empty sycn to org")
        }

        throw NullPointerException()
    }

    override fun getHouseAndUpdate(org: Organization, _id: String,databaseDao : DatabaseDao) {
        printDebug("Get house house _id = $_id")
        val data = restService!!.getHouse(orgId = org.id,authkey = "Bearer " + org.token!!,_id = _id).execute()
        printDebug("\tRespond code ${data.code()}")
        val house = data.body()?: throw NotFoundException("ไม่มี เลขบ้าน getHouse")
        printDebug("\t From house cloud _id = ${house._id} house No. ${house.no}")
        if (house._sync) return

        databaseDao.upateHouse(house)
        printDebug("\tUpdate house to database and sync = true")
        house._sync=true


        printDebug("\tPut new house to cloud")
        restService.putHouse(orgId = org.id, authkey = "Bearer " + org.token!!, _id = _id,house = house).execute()

    }

    override fun syncActionUpdateStatus(org: Organization, actionId: UUID, status: ActionHouse.STATUS) {

        printDebug("syncActionUpdateStatus")
        restService!!.putSyncUpdateStatus(orgId = org.id,
          actionId = actionId,
          status = status,
          authkey = "Bearer " + org.token!!).execute()

    }

    override fun putUser(userInfoList: ArrayList<User>, org: Organization) {

        //val restService = ApiFactory().buildApiClient(Config.baseUrlRest)
        restService!!.regisUser(user = userInfoList, orgId = org.id, authkey = "Bearer " + org.token!!).execute()


    }

    override fun putFirebaseToken(firebaseToken: FirebaseToken, org: Organization) {
        printDebug("PutFirebase Token to Server")
        printDebug("\torgId ${org.id} orgToken ${org.token}")
        restService!!.createFirebaseToken(orgId = org.id,
          authkey = "Bearer " + org.token!!,
          firebaseToken = firebaseToken
        ).execute()
    }

    override fun putHouse(houseList: List<Address>, org: Organization) {

        //houseList.forEach { printDebug(it) }
        val fixrow = 100

        val count = houseList.size
        val split = count / fixrow
        val splitmod = count % fixrow


        printDebug("House upload size $count")

        for (i in 0..(split - 1)) {
            val tempUpload = arrayListOf<Address>()
            val tempStamp = i * fixrow
            for (j in 0..fixrow) {
                tempUpload.add(houseList[tempStamp + j])
            }
            printDebug("fixrow $fixrow split $split splitmod $splitmod i $i")

            Thread(object : Runnable {
                override fun run() {
                    restService!!.createHouse(orgId = org.id, authkey = "Bearer " + org.token!!, houseList = tempUpload).execute()
                }
            }).start()

            Thread.sleep(1000)

        }
        if (splitmod != 0) {
            val tempUpload = arrayListOf<Address>()
            val tempStamp = split * fixrow
            for (i in 0..(splitmod - 1)) {
                tempUpload.add(houseList[tempStamp + i])
            }
            restService!!.createHouse(orgId = org.id, authkey = "Bearer " + org.token!!, houseList = tempUpload).execute()
        }

        printDebug("End update House")
        Thread.sleep(10000)
        //restService!!.createHouse(orgId = org.id, authkey = "Bearer " + org.token!!,houseList = houseList).execute()


    }


    override fun putPerson(personList: List<Person>, org: Organization) {
        restService!!.createPerson(orgId = org.id,
          authkey = "Bearer " + org.token!!,
          personList = personList).execute()

    }

    override fun putChronic(chronicList: List<Chronic>, org: Organization) {
        restService!!.createChronic(orgId = org.id,
          authkey = "Bearer " + org.token!!,
          chronicList = chronicList).execute()

    }

    override fun registerOrganization(organization: Organization, url: String): Organization {
        CentralMessageMaorgUpdatenageV1.organization = organization
        urlBase = url

        //val organization2: Organization = organization.toJson().httpPost(url).body()!!.string().fromJson()
        val restService = ApiFactory().buildApiClient(Config.baseUrlRest)
        val org = restService!!.regisOrg(organization).execute().body()

        printDebug("Client registerOrg " + org)
        //Thread.sleep(3000)

        if (org != null) {
            CentralMessageMaorgUpdatenageV1.organization = org
            return org
        }
        throw ClassNotFoundException()
    }


}



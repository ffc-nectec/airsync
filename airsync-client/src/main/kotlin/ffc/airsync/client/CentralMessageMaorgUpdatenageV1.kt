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
import ffc.model.*
import java.util.*
import javax.ws.rs.NotFoundException
import kotlin.collections.ArrayList


class CentralMessageMaorgUpdatenageV1 : CentralMessageManage {


    val restService = ApiFactory().buildApiClient(Config.baseUrlRest)

    companion object {
        var organization: Organization? = null
        var urlBase: String? = null
    }


    override fun getHouseAndUpdate(org: Organization, _id: String, databaseDao: DatabaseDao) {
        printDebug("Get house house _id = $_id")
        val data = restService!!.getHouse(orgId = org.id, authkey = "Bearer " + org.token!!, _id = _id).execute()
        printDebug("\tRespond code ${data.code()}")
        val house = data.body() ?: throw NotFoundException("ไม่มี เลขบ้าน getHouse")
        printDebug("\t From house cloud _id = ${house._id} house No. ${house.no}")
        if (house._sync) return

        databaseDao.upateHouse(house)
        printDebug("\tUpdate house to database and sync = true")
        house._sync = true


        printDebug("\tPut new house to cloud")
        restService.putHouse(orgId = org.id, authkey = "Bearer " + org.token!!, _id = _id, house = house).execute()

    }


    override fun putUser(userInfoList: ArrayList<User>, org: Organization) {
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

        //restService!!.createHouse(orgId = org.id, authkey = "Bearer " + org.token!!,houseList = houseList).execute()

        SplitUpload.upload(300, houseList, object : SplitUpload.HowToSendCake<Address> {
            override fun send(cakePlate: ArrayList<Address>) {
                restService!!.createHouse(orgId = org.id,
                  authkey = "Bearer " + org.token!!,
                  houseList = cakePlate).execute()
            }
        })

    }


    override fun putPerson(personList: List<Person>, org: Organization) {

        SplitUpload.upload(300, personList, object : SplitUpload.HowToSendCake<Person> {
            override fun send(cakePlate: ArrayList<Person>) {
                restService!!.createPerson(orgId = org.id,
                  authkey = "Bearer " + org.token!!,
                  personList = cakePlate).execute()

            }
        })


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



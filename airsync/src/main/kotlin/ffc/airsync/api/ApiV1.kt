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

package ffc.airsync.api

import ffc.airsync.Config
import ffc.airsync.db.DatabaseDao
import ffc.airsync.utils.printDebug
import ffc.entity.House
import ffc.entity.Organization
import ffc.entity.Person
import ffc.entity.Token
import ffc.entity.User
import ffc.entity.gson.toJson
import ffc.entity.healthcare.Chronic
import java.net.SocketTimeoutException
import javax.xml.bind.DatatypeConverter

class ApiV1 : Api {

    val restService = ApiFactory().buildApiClient(Config.baseUrlRest)

    companion object {
        lateinit var organization: Organization
        lateinit var urlBase: String
        lateinit var token: Token
    }

    private val oAuth2Token: String
        get() = "Bearer " + token.token

    override fun putFirebaseToken(firebaseToken: HashMap<String, String>, org: Organization) {
        val status = restService!!.createFirebaseToken(
            orgId = org.id,
            authkey = oAuth2Token,
            firebaseToken = firebaseToken
        ).execute()
        if (status.code() != 201) printDebug("FireBase is not set $status")
        printDebug("\tRespond filebase put $status")
    }

    override fun syncHouseToCloud(house: House, org: Organization) {
        restService.putHouse(orgId = org.id, authkey = oAuth2Token, _id = house.id, house = house).execute()
    }

    override fun syncHouseFromCloud(org: Organization, _id: String, databaseDao: DatabaseDao) {
        printDebug("Sync From Cloud get house house _id = $_id")
        val data = restService!!.getHouse(orgId = org.id, authkey = oAuth2Token, _id = _id).execute()
        printDebug("\tRespond code ${data.code()}")
        val house = data.body() ?: throw IllegalArgumentException("ไม่มี เลขบ้าน getHouse")
        printDebug("\t From house cloud _id = ${house.id} house No. ${house.no}")
        if (house.link?.isSynced == true) return

        databaseDao.upateHouse(house)
        printDebug("\tUpdate house to database and sync = true")
        house.link?.isSynced = true

        printDebug("\tPut new house to cloud")
        restService.putHouse(orgId = org.id, authkey = oAuth2Token, _id = _id, house = house).execute()
    }

    override fun putUser(userInfoList: List<User>, org: Organization): List<User> {
        val respond = restService.regisUser(user = userInfoList, orgId = org.id, authkey = oAuth2Token).execute()
        return respond.body() ?: arrayListOf()
    }

    override fun putHouse(houseList: List<House>, org: Organization): List<House> {
        val houseLastUpdate = arrayListOf<House>()
        UploadSpliter.upload(300, houseList, object : UploadSpliter.HowToSendCake<House> {
            override fun send(cakePlate: ArrayList<House>) {
                val respond = restService.createHouse(
                    orgId = org.id,
                    authkey = oAuth2Token,
                    houseList = cakePlate
                ).execute()
                if (respond.code() != 201) throw IllegalAccessException("Cannot Login ${respond.code()}")
                val houseList = respond.body() ?: arrayListOf()
                houseLastUpdate.addAll(houseList)
            }
        })
        return houseLastUpdate
    }

    override fun putPerson(persons: List<Person>, org: Organization): List<Person> {

        val personLastUpdate = arrayListOf<Person>()
        UploadSpliter.upload(300, persons, object : UploadSpliter.HowToSendCake<Person> {
            override fun send(cakePlate: ArrayList<Person>) {
                val respond = restService.createPerson(
                    orgId = org.id,
                    authkey = oAuth2Token,
                    personList = cakePlate
                ).execute()
                if (respond.code() != 201) throw IllegalAccessException("Cannot Login ${respond.code()}")
                personLastUpdate.addAll(respond.body() ?: arrayListOf())
            }
        })
        return personLastUpdate
    }

    override fun putChronic(chronicList: List<Chronic>, org: Organization) {
        restService.createChronic(
            orgId = org.id,
            authkey = oAuth2Token,
            chronicList = chronicList
        ).execute()
    }

    override fun registerOrganization(organization: Organization, url: String): Organization {
        Companion.organization = organization
        urlBase = url
        wakeCloud()

        if (isEverRegister(organization)) {
            token = organization.bundle["token"] as Token
            Companion.organization = organization
        } else {
            Companion.organization = regisOrgToCloud(organization)

            val user = organization.users[0]
            val authStr = user.name + ":" + user.password
            val authEncoded = DatatypeConverter.printBase64Binary(authStr.toByteArray())
            val authorization = "Basic $authEncoded"
            val tokenFromServer = restService.loginOrg(Companion.organization.id, authorization).execute().body()
                ?: throw Exception("ไม่สามารถ Login org ได้")
            printDebug("\tToken = ${tokenFromServer.toJson()}")
            token = tokenFromServer
            Companion.organization.bundle["token"] = tokenFromServer

            printDebug("Client update registerOrg from cloud ${Companion.organization.toJson()}")
        }

        return Companion.organization
    }

    private fun wakeCloud() {
        var count = 1
        val limitCount = 5
        var cloudStatusDown = true
        while (cloudStatusDown && count++ <= limitCount) {
            try {
                printDebug("Wake cloud loop ${count - 1} in $limitCount")
                restService.checkCloud()
                cloudStatusDown = false
            } catch (ignore: SocketTimeoutException) {
                Thread.sleep(3000)
            }
        }
    }

    private fun isEverRegister(organization: Organization) = organization.bundle["token"] != null

    private fun regisOrgToCloud(organization: Organization): Organization {
        var restOrg: Organization? = restService.regisOrg(organization).execute().body()
        var count = 1
        val limitCount = 5
        while (restOrg == null && count++ <= limitCount) {
            println("Faild organization re-register wait.... 3 sec count: ${count - 1} in $limitCount")
            Thread.sleep(3000)
            restOrg = restService.regisOrg(organization).execute().body()
        }
        if (restOrg == null) throw IllegalStateException("ไม่มีข้อมูลการลงทะเบียน Org")

        return restOrg
    }
}

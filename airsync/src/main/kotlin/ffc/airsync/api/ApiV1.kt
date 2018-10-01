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
import ffc.airsync.utils.isTempId
import ffc.airsync.utils.printDebug
import ffc.entity.House
import ffc.entity.Organization
import ffc.entity.Person
import ffc.entity.Token
import ffc.entity.User
import ffc.entity.gson.toJson
import retrofit2.dsl.enqueue
import java.net.SocketTimeoutException
import javax.xml.bind.DatatypeConverter

class ApiV1(val persons: List<Person>, val houses: List<House>, val users: List<User>, val pcucode: StringBuilder) :
    Api {
    val restService = ApiFactory().buildApiClient(Config.baseUrlRest)

    companion object {
        lateinit var organization: Organization
        lateinit var urlBase: String
        lateinit var token: Token
    }

    private val oAuth2Token: String
        get() = "Bearer " + token.token

    override fun putFirebaseToken(firebaseToken: HashMap<String, String>) {
        restService.createFirebaseToken(
            orgId = organization.id,
            authkey = oAuth2Token,
            firebaseToken = firebaseToken
        ).enqueue {
            onSuccess { printDebug("Success bind firebase to cloud") }
        }
    }

    override fun syncHealthCareFromCloud(id: String, dao: DatabaseDao) {
        val data = restService.getHomeVisit(orgId = organization.id, authkey = oAuth2Token, id = id).execute()

        if (data.code() != 200) {
            printDebug("Not success get healthcare code=${data.code()}")
            return
        }
        val healthCareService = data.body()!!

        val pcucode = this.pcucode.toString().trim()

        if (healthCareService.patientId.isTempId() ||
            healthCareService.providerId.isTempId()
        )
            throw IllegalAccessException(
                "Health Care Service provider or patient isTempId "
            )

        val patient = persons.find {
            it.id == healthCareService.patientId
        }!!

        printDebug("partian id ${(patient.link!!.keys["pid"] as String).toLong()}")

        val patientHos = dao.findPerson(pcucode, (patient.link!!.keys["pid"] as String).toLong())
        patient.bundle.putAll(patientHos.bundle)

        val provider = users.find {
            it.id == healthCareService.providerId
        }!!

        dao.createHomeVisit(
            healthCareService,
            pcucode,
            pcucode,
            patient,
            provider.name
        )
    }

    override fun syncHouseToCloud(house: House) {
        restService.putHouse(orgId = organization.id, authkey = oAuth2Token, _id = house.id, house = house).execute()
    }

    override fun syncHouseFromCloud(_id: String, databaseDao: DatabaseDao) {
        printDebug("Sync From Cloud get house house _id = $_id")
        val data = restService.getHouse(orgId = organization.id, authkey = oAuth2Token, _id = _id).execute()
        printDebug("\tRespond code ${data.code()}")
        val house = data.body() ?: throw IllegalArgumentException("ไม่มี เลขบ้าน getHouse")
        printDebug("\t From house cloud _id = ${house.id} house No. ${house.no}")
        if (house.link?.isSynced == true) return

        databaseDao.upateHouse(house)
        printDebug("\tUpdate house to database and sync = true")
        house.link?.isSynced = true

        printDebug("\tPut new house to cloud")
        restService.putHouse(orgId = organization.id, authkey = oAuth2Token, _id = _id, house = house).execute()
    }

    override fun putUser(userInfoList: List<User>): List<User> {
        val respond =
            restService.regisUser(user = userInfoList, orgId = organization.id, authkey = oAuth2Token).execute()
        return respond.body() ?: arrayListOf()
    }

    override fun putHouse(houseList: List<House>): List<House> {
        val houseLastUpdate = arrayListOf<House>()
        UploadSpliter.upload(100, houseList, object : UploadSpliter.HowToSendCake<House> {
            override fun send(cakePlate: ArrayList<House>) {
                val respond = restService.createHouse(
                    orgId = organization.id,
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

    override fun putPerson(persons: List<Person>): List<Person> {
        val personLastUpdate = arrayListOf<Person>()
        UploadSpliter.upload(200, persons, object : UploadSpliter.HowToSendCake<Person> {
            override fun send(cakePlate: ArrayList<Person>) {
                val respond = restService.createPerson(
                    orgId = organization.id,
                    authkey = oAuth2Token,
                    personList = cakePlate
                ).execute()
                if (respond.code() != 201) throw IllegalAccessException("Cannot Login ${respond.code()}")
                personLastUpdate.addAll(respond.body() ?: arrayListOf())
            }
        })
        return personLastUpdate
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

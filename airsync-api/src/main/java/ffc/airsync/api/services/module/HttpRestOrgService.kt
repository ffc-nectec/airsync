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

package ffc.airsync.api.services.module

import ffc.airsync.api.dao.DaoFactory
import ffc.model.*
import java.util.*
import javax.ws.rs.NotFoundException
import kotlin.collections.ArrayList


class HttpRestOrgService : OrgService {

    private constructor()

    companion object {
        val instant = HttpRestOrgService()
    }

    val pcuDao = DaoFactory().buildPcuDao()
    val orgUser = DaoFactory().buildOrgUserDao()
    val houseDao = DaoFactory().buildHouseDao()
    val tokenMobile = DaoFactory().buildTokenMobileMapDao()
    val personDao = DaoFactory().buildPersonDao()
    val chronicDao = DaoFactory().buildChronicDao()


    override fun register(organization: Organization, lastKnownIp: String): Organization {

        organization.token = UUID.randomUUID().toString()
        organization.lastKnownIp = lastKnownIp
        organization.socketUrl = "ws://127.0.0.1:8080/airsync"
        //organization.socketUrl="ws://188.166.249.72/airsync"

        pcuDao.insert(organization)
        return organization
    }


    override fun createUser(token: String, orgId: String, userList: ArrayList<User>) {
        val org = checkToken(token, orgId)

        userList.forEach {
            println("insert username " + org.name + " User = " + it.username)
            orgUser.insert(it, org)
        }
    }

    override fun getMyOrg(ipAddress: String): List<Organization> {

        val pcuReturn = pcuDao.findByIpAddress(ipAddress)
        if (pcuReturn.isNotEmpty())
            return pcuReturn
        throw NotFoundException("ไม่มีข้อมูลลงทะเบียน")

    }

    override fun getOrg(): List<Organization> {
        val pcuReturn = pcuDao.find()
        return pcuReturn
    }

    override fun orgUserAuth(id: String, user: String, pass: String): TokenMessage {
        val checkUser = orgUser.isAllowById(User(user, pass), id)

        if (checkUser) {
            val org = pcuDao.findById(id)
            if (org == null) throw NotFoundException()

            val token = UUID.randomUUID().toString()

            tokenMobile.insert(TokenMap(token = token, uuid = org.uuid, user = user))

            return TokenMessage(token)
        }
        throw NotFoundException()
    }

    override fun createHouse(token: String, orgId: String, houseList: List<Address>) {
        val org = checkToken(token, orgId)
        houseDao.insert(org.uuid, houseList)
    }

    override fun createPerson(token: String, orgId: String, personList: List<Person>) {
        val org = checkToken(token, orgId)
        personDao.insert(org.uuid, personList)

    }

    override fun createChronic(token: String, orgId: String, chronicList: List<Chronic>) {
        val org = checkToken(token, orgId)
        chronicDao.insert(org.uuid, chronicList)
    }

    override fun sendEventGetData(uuid: UUID) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        //
    }

    override fun getData(uuid: UUID): Message<QueryAction> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun checkToken(token: String, orgId: String): Organization {
        val org = pcuDao.findByToken(token)

        if (org == null) throw NotFoundException()
        if (org.id != orgId) throw NotFoundException()

        return org
    }
}

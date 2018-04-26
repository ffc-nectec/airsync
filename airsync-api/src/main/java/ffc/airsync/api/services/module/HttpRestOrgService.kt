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

import ffc.model.*
import java.util.*
import javax.ws.rs.NotAuthorizedException
import javax.ws.rs.NotFoundException
import kotlin.collections.ArrayList


class HttpRestOrgService : OrgService {

    private constructor()

    companion object {
        val instant = HttpRestOrgService()
    }


    override fun removeOrganize(token: String, orgId: String) {
        val org = getOrgByOrgToken(token, orgId)
        printDebug("Remove org id = $orgId == ${org.id}")
        if (org.id != orgId) throw NotAuthorizedException("Not Auth")
        val uuidForRemove = UUID.fromString(org.uuid.toString())
        orgDao.removeByOrgUuid(uuidForRemove)
        orgUser.removeByOrgUuid(uuidForRemove)
        houseDao.removeByOrgUuid(uuidForRemove)
        tokenMobile.removeByOrgUuid(uuidForRemove)
        personDao.removeByOrgUuid(uuidForRemove)
        chronicDao.removeByOrgUuid(uuidForRemove)

    }

    override fun getPerson(token: String, orgId: String): List<Person> {

        val tokenObj = getOrgByMobileToken(UUID.fromString(token.trim()), orgId)
        val personList = personDao.find(orgUuid = tokenObj.uuid)
        val personReturn = arrayListOf<Person>()


        var lmitLoop = 0

        personList.forEach {
            if (lmitLoop < 100) {
                lmitLoop++

                val person = it.data
                val chronicPerson = chronicDao.filterByPersonPid(tokenObj.uuid, it.data.pid!!.toInt())
                val chronicList = arrayListOf<Chronic>()

                if (chronicPerson.isNotEmpty())
                    chronicPerson.forEach {
                        printDebug("It pid = ${it.data.pid} Person pid = ${person.pid}")
                        chronicList.add(it.data)
                    }
                person.chronics = chronicList


                if (person.houseId != null) {
                    val housePerson = houseDao.findByHouseId(tokenObj.uuid, person.houseId!!)
                    person.house = housePerson?.data
                }


                personReturn.add(person)
            }
        }




        return personReturn
    }

    override fun register(organization: Organization, lastKnownIp: String): Organization {

        organization.token = UUID.randomUUID().toString()
        organization.lastKnownIp = lastKnownIp
        organization.socketUrl = "ws://127.0.0.1:8080/airsync"
        //organization.socketUrl="ws://188.166.249.72/airsync"

        orgDao.insert(organization)
        return organization
    }


    override fun createUser(token: String, orgId: String, userList: ArrayList<User>) {
        val org = getOrgByOrgToken(token, orgId)

        userList.forEach {
            printDebug("insert username " + org.name + " User = " + it.username)
            orgUser.insert(it, org)
        }
    }

    override fun getMyOrg(ipAddress: String): List<Organization> {

        printDebug("ip address get my org $ipAddress")
        val pcuReturn = orgDao.findByIpAddress(ipAddress)
        if (pcuReturn.isEmpty())
            throw NotFoundException("ไม่มีข้อมูลลงทะเบียน")
        return pcuReturn


    }

    override fun getOrg(): List<Organization> {
        val pcuReturn = orgDao.find()
        if (pcuReturn.isEmpty()) throw NotFoundException("ไม่มีข้อมูลลงทะเบียน")
        return pcuReturn
    }

    override fun orgUserAuth(id: String, user: String, pass: String): TokenMessage {
        val checkUser = orgUser.isAllowById(User(user, pass), id)

        if (checkUser) {
            val org = orgDao.findById(id)
            if (org == null) throw NotAuthorizedException("Not org")

            val token = UUID.randomUUID()

            tokenMobile.insert(token = token,
              uuid = org.uuid,
              user = user,
              id = id.toInt())

            return TokenMessage(token.toString())
        }
        throw NotAuthorizedException("Not Auth")
    }


    override fun createPerson(token: String, orgId: String, personList: List<Person>) {
        val org = getOrgByOrgToken(token, orgId)
        personDao.insert(org.uuid, personList)

    }

    override fun createChronic(token: String, orgId: String, chronicList: List<Chronic>) {
        val org = getOrgByOrgToken(token, orgId)
        chronicDao.insert(org.uuid, chronicList)
    }


}

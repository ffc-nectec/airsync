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

class OrgServiceHttpRestService : OrgService {

    val pcuDao = DaoFactory().buildPcuDao()
    val mobileUserAuthDao = DaoFactory().buildUserAuthDao()
    val orgUser = DaoFactory().buildOrgUserDao()


    override fun register(organization: Organization, lastKnownIp: String): Organization {

        organization.orgToken = UUID.randomUUID().toString()
        organization.lastKnownIp=lastKnownIp
        pcuDao.insert(organization)
        return organization
    }

    override fun getMobileUser(organization: Organization): List<UserInfo> {
        val mobileUserList = mobileUserAuthDao.findByPcu(organization)
        return mobileUserList
    }

    override fun setUserPass(userInfo: UserInfo) {
        mobileUserAuthDao.updateStatusPass(userInfo)
    }

    override fun setUserNotPass(userInfo: UserInfo) {
        mobileUserAuthDao.updateStatusNotPass(userInfo)
    }

    override fun createUser(token: String, orgId: String, userList: ArrayList<User>) {
        val org = pcuDao.findByToken(token)

        if(org==null) throw NotFoundException()
        if(org.id != orgId) throw NotFoundException()

        userList.forEach {
            println("insert user "+ org.name +" User = "+it.user)
            orgUser.insert(it,org)
        }
    }

    override fun getMyOrg(ipAddress: String): List<Organization> {
        return pcuDao.findByIpAddress(ipAddress)
    }

    override fun sendEventGetData(uuid: UUID) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        //

    }

    override fun getData(uuid: UUID): Message<QueryAction> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

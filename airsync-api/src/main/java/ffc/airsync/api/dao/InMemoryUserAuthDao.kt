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

package ffc.airsync.api.dao

import ffc.model.MobileRoutePcu
import ffc.model.Organization
import ffc.model.UserInfo
import java.util.*

class InMemoryUserAuthDao :UserAuthDao {

    private constructor()
    private val userList = arrayListOf<UserInfo>()
    private val userPass = arrayListOf<MobileRoutePcu>()


    companion object {
        val instance = InMemoryUserAuthDao()

    }

    override fun insert(userInfo: UserInfo) {

        remove(userInfo)
        userList.add(userInfo)

    }

    override fun find(userInfo: UserInfo) : UserInfo? {
        val userAuth = userList.find { it.getKey() == userInfo.getKey() }
        return userAuth
    }

    override fun remove(userInfo: UserInfo) {
        userList.removeIf {
            it.getKey().equals(userInfo.getKey())
        }
    }

    override fun findByPcu(organization: Organization): List<UserInfo> {

        val userAuthList = ArrayList<UserInfo>()

        userList.forEach {
            if (it.orgUuid == organization.uuid){
                userAuthList.add(it)
            }
        }

        if (userAuthList.size<1) throw NoSuchElementException("Not Found UserInfo Auth")

        return userAuthList

    }

    override fun findRouteByMobileUuid(mobileUUID: UUID): MobileRoutePcu {

        val mobileRoutePcu = userPass.find { it.mobileUuid==mobileUUID }
        if(mobileRoutePcu != null)
            return mobileRoutePcu

        throw NoSuchElementException("Not found mobile routing ownAction orgUuid.")

    }

    override fun updateStatusPass(userInfo: UserInfo) {

        val userAuth = find(userInfo)
        if(userAuth != null) {
            updateStatusPass(MobileRoutePcu(mobileUuid = userAuth.mobileUuid, pcuUuid = userAuth.orgUuid))
        }

    }
    override fun updateStatusPass(mapMobileObject: MobileRoutePcu) {
        userList.removeIf { it.mobileUuid==mapMobileObject.mobileUuid }
        userPass.add(mapMobileObject)
    }

    override fun updateStatusNotPass(userInfo: UserInfo) {

        updateStatusNotPass(MobileRoutePcu(mobileUuid = userInfo.mobileUuid , pcuUuid = userInfo.orgUuid))
    }



    override fun updateStatusNotPass(mapMobileObject: MobileRoutePcu) {
        userPass.removeIf { it==mapMobileObject }
    }
}

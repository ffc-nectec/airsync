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
import ffc.model.MobileUserAuth
import ffc.model.Pcu
import java.util.*

class InMemoryUserAuthDao :UserAuthDao {

    private constructor()
    private val userList = arrayListOf<MobileUserAuth>()
    private val userPass = arrayListOf<MobileRoutePcu>()


    companion object {
        val instance = InMemoryUserAuthDao()

    }

    override fun insert(mobileUserAuth: MobileUserAuth) {

        remove(mobileUserAuth)
        userList.add(mobileUserAuth)

    }

    override fun find(mobileUserAuth: MobileUserAuth) : MobileUserAuth? {
        val userAuth = userList.find { it.getKey() == mobileUserAuth.getKey() }
        return userAuth
    }

    override fun remove(mobileUserAuth: MobileUserAuth) {
        userList.removeIf {
            it.getKey().equals(mobileUserAuth.getKey())
        }
    }

    override fun findByPcu(pcu: Pcu): List<MobileUserAuth> {

        val userAuthList = ArrayList<MobileUserAuth>()

        userList.forEach {
            if (it.pcu.uuid == pcu.uuid){
                userAuthList.add(it)
            }
        }

        if (userAuthList.size<1) throw NoSuchElementException("Not Found User Auth")

        return userAuthList

    }

    override fun findRouteByMobileUuid(mobileUUID: UUID): MobileRoutePcu {

        val mobileRoutePcu = userPass.find { it.mobileUuid==mobileUUID }
        if(mobileRoutePcu != null)
            return mobileRoutePcu

        throw NoSuchElementException("Not found mobile routing to pcu.")

    }

    override fun updateStatusPass(mobileUserAuth: MobileUserAuth) {

        val userAuth = find(mobileUserAuth)
        if(userAuth != null) {
            updateStatusPass(MobileRoutePcu(mobileUuid = userAuth.mobileUuid, pcuUuid = userAuth.pcu.uuid))
        }

    }
    override fun updateStatusPass(mapMobileObject: MobileRoutePcu) {
        userList.removeIf { it.mobileUuid==mapMobileObject.mobileUuid }
        userPass.add(mapMobileObject)
    }

    override fun updateStatusNotPass(mobileUserAuth: MobileUserAuth) {

        updateStatusNotPass(MobileRoutePcu(mobileUuid = mobileUserAuth.mobileUuid , pcuUuid = mobileUserAuth.pcu.uuid))
    }



    override fun updateStatusNotPass(mapMobileObject: MobileRoutePcu) {
        userPass.removeIf { it==mapMobileObject }
    }
}

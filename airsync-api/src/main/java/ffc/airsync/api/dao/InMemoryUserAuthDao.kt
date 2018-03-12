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

import ffc.model.MobileMapPcuWithUuid
import ffc.model.MobileUserAuth
import ffc.model.Pcu

class InMemoryUserAuthDao :UserAuthDao {

    private constructor()
    private val userList = arrayListOf<MobileUserAuth>()
    private val userPass = arrayListOf<MobileMapPcuWithUuid>()


    companion object {
        val instance = InMemoryUserAuthDao()

    }

    override fun insert(mobileUserAuth: MobileUserAuth) {

        remove(mobileUserAuth)
        userList.add(mobileUserAuth)

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

    override fun updateStatusPass(mobileUserAuth: MobileUserAuth) {
        val userAuth = userList.find { it.getKey() == mobileUserAuth.getKey() }
        if(userAuth != null) {
            updateStatusPass(MobileMapPcuWithUuid(mobileUuid = userAuth.mobileUuid, pcuUuid = userAuth.pcu.uuid))
        }

    }
    override fun updateStatusPass(mapMobileObject: MobileMapPcuWithUuid) {
        userPass.add(mapMobileObject)
    }

    override fun updateStatusNotPass(mobileUserAuth: MobileUserAuth) {

        updateStatusNotPass(MobileMapPcuWithUuid(mobileUuid = mobileUserAuth.mobileUuid , pcuUuid = mobileUserAuth.pcu.uuid))
    }



    override fun updateStatusNotPass(mapMobileObject: MobileMapPcuWithUuid) {
        userPass.removeIf { it==mapMobileObject }
    }
}

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

interface UserAuthDao {
    fun insert(mobileUserAuth: MobileUserAuth)
    fun remove(mobileUserAuth: MobileUserAuth)

    fun findByPcu(pcu: Pcu) :List<MobileUserAuth>

    fun updateStatusPass(mobileUserAuth: MobileUserAuth)
    fun updateStatusPass(mapMobileObject : MobileMapPcuWithUuid)
    fun updateStatusNotPass(mobileUserAuth: MobileUserAuth)
    fun updateStatusNotPass(mapMobileObject : MobileMapPcuWithUuid)

}

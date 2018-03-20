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

interface UserAuthDao {
    fun insert(userInfo: UserInfo)
    fun remove(userInfo: UserInfo)
    fun find(userInfo: UserInfo): UserInfo?

    fun findRouteByMobileUuid(mobileUUID: UUID) :MobileRoutePcu

    fun findByPcu(organization: Organization) :List<UserInfo>

    fun updateStatusPass(userInfo: UserInfo)
    fun updateStatusPass(mapMobileObject : MobileRoutePcu)
    fun updateStatusNotPass(userInfo: UserInfo)
    fun updateStatusNotPass(mapMobileObject : MobileRoutePcu)

}

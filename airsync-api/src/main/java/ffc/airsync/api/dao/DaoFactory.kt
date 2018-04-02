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

import java.util.*

class DaoFactory(val dev: Boolean = true) {


    fun buildMobileDao(): MobileDao = if (dev) InMemoryMobileDao.instance else EsMobileDao()
    fun buildPcuDao(): OrgDao = if (dev) InMemoryOrgDao.instance else EsOrgDao()
    fun buildMessageActionDao(): MessageActionDao = InMemoryMessageActionDao.instance
    //fun buildUserAuthDao(): UserAuthDao = InMemoryUserAuthDao.instance
    fun buildOrgUserDao(): OrgUserDao = InMemoryOrgUserDao.INSTANT

    fun buildHouseDao() : HouseDao = InMemoryHouseDao.instant

    fun buildPersonDao():PersonDao = InMemoryPersonDao.instant



    var tokenOrgMap: TokenMapDao<UUID>? = null
    var tokenMobileMap: TokenMapDao<UUID>? = null

    fun buildTokenOrgMapDao(): TokenMapDao<UUID> {
        if (tokenOrgMap == null) {
            tokenOrgMap = InMemoryTokenMapDao()
        }
        return tokenOrgMap as TokenMapDao<UUID>
    }

    fun buildTokenMobileMapDao(): TokenMapDao<UUID> {

        if (tokenMobileMap == null) {
            tokenMobileMap = InMemoryTokenMapDao()
        }
        return tokenMobileMap as TokenMapDao<UUID>
    }

}

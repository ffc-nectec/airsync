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
import ffc.model.Organization
import ffc.model.StorageOrg
import ffc.model.printDebug
import java.util.*
import javax.ws.rs.NotAuthorizedException

val orgDao = DaoFactory().buildPcuDao()
val tokenMobile = DaoFactory().buildTokenMobileMapDao()
val chronicDao = DaoFactory().buildChronicDao()
val personDao = DaoFactory().buildPersonDao()
val orgUser = DaoFactory().buildOrgUserDao()
val houseDao = DaoFactory().buildHouseDao()

fun getOrgByOrgToken(token: String, orgId: String): Organization {
    printDebug("getOrgByOrgToken $token")
    val org = orgDao.findByToken(token)
    if (org.id != orgId) {
        printDebug("org ไม่ตรงกัน")
        throw throw NotAuthorizedException("Not Auth")
    }

    return org
}

fun getOrgByMobileToken(token: UUID, orgId: String): StorageOrg<UUID> {
    printDebug("Befor check token")
    val orgUuid = tokenMobile.find(token)
    //if (orgUuid ) throw NotFoundException()
    if (orgUuid.id != orgId.toInt()) throw NotAuthorizedException("Not Auth")

    printDebug("Token pass org ")

    return orgUuid
}

/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package ffc.airsync.api.organization

import ffc.airsync.api.organization.prop.FfcOrganizationProperty
import ffc.airsync.api.organization.prop.OrganizationProperty
import ffc.airsync.db.DatabaseDao
import ffc.airsync.utils.getLogger
import ffc.entity.Link
import ffc.entity.Organization
import ffc.entity.System
import ffc.entity.User
import ffc.entity.update
import java.io.File
import java.util.UUID

internal class LocalOrganization(
    val dao: DatabaseDao,
    logConfig: File = File("C:\\Program Files\\JHCIS\\MySQL\\data\\ffcProperty.cnf")
) {
    private val logger by lazy { getLogger(this) }
    private val prop: OrganizationProperty = FfcOrganizationProperty(logConfig)
    val organization: Organization

    init {
        organization = getOrganizationDetail(orgId)
    }

    var token: String
        get() = prop.token ?: ""
        set(value) {
            prop.token = value
        }

    var orgId: String
        get() = prop.organizationId ?: ""
        set(value) {
            prop.organizationId = value
        }

    var userOrg: User
        get() = prop.adminUser!!
        set(value) {
            prop.adminUser = value
        }

    private fun getOrganizationDetail(orgId: String): Organization {
        val org: Organization = if (orgId.isNotEmpty()) {
            logger.info("Get organization id from config.")
            Organization(orgId)
        } else {
            logger.info("Create new organization")
            Organization()
        }
        with(org) {
            logger.trace("Get organization detail from database.")
            val detail = dao.getDetail()
            val hosId = detail["pcucode"] ?: ""

            name = detail["name"] ?: ""
            name = name.replace(Regex("""[\:\/\?\#\[\]\@\!\$\&\'\(\)\*\+\,\;\=\<\>\{\}\|\`\^\\\"\% \.]"""), "")
            displayName = detail["name"] ?: ""
            tel = detail["tel"]
            address = detail["province"]
            link = Link(System.JHICS).apply {
                keys["pcucode"] = hosId
            }
            users.add(createAirSyncUser(hosId))
            update { }
        }
        return org
    }

    private fun createAirSyncUser(hosId: String): User = User().update {
        logger.debug("Get user from database")
        name = "airsync$hosId"
        password = UUID.randomUUID().toString().replace("-", "")
        roles.add(User.Role.ADMIN)
        roles.add(User.Role.SYNC_AGENT)
    }
}

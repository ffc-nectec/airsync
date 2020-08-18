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

import ffc.airsync.db.DatabaseDao
import ffc.airsync.utils.getLogger
import ffc.entity.Link
import ffc.entity.Organization
import ffc.entity.System
import ffc.entity.User
import ffc.entity.update
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties
import java.util.UUID

class LocalOrganization(
    val dao: DatabaseDao,
    var logConfig: String = "C:\\Program Files\\JHCIS\\MySQL\\data\\ffcProperty.cnf"
) {
    private val logger by lazy { getLogger(this) }

    private lateinit var properties: Properties
    val organization: Organization

    init {
        loadProperty()
        organization = getOrganizationDetail(orgId)
    }

    var token: String
        get() = getProperty("token")
        set(value) = setProperty("token", value)

    var orgId: String
        get() = getProperty("orgId")
        set(value) = setProperty("orgId", value)

    var userOrg: User
        get() = User(getProperty("userIdOrg")).apply {
            name = getProperty("userOrg")
        }
        set(value) {
            setProperty("userIdOrg", value.id)
            setProperty("userOrg", value.name)
        }

    var everPutData: Boolean
        get() = getProperty("EverPutData").toBoolean()
        set(value) {
            setProperty("EverPutData", value.toString())
        }

    fun getProperty(key: String): String {
        loadProperty()
        return properties.getProperty(key, "")
    }

    fun setProperty(key: String, value: String) {
        properties.setProperty(key, value)
        saveProperty()
    }

    private fun saveProperty() {
        properties.store(FileOutputStream(logConfig), null)
    }

    private fun loadProperty() {
        logger.trace("Load config property from file.")
        val conf = Properties()
        try {
            conf.load(FileInputStream(logConfig))
        } catch (ignore: java.io.FileNotFoundException) {
            logger.debug("ไม่พบ config file ของ airsync อาจเป็นเพราะเข้าใช้งานครั้งแรก ระบบจะสร้างให้อัตโนมัติ")
        }
        properties = conf
    }

    private fun getOrganizationDetail(orgId: String): Organization {
        logger.info("Get Organization detail")
        val org: Organization = if (orgId.isNotEmpty()) {
            Organization(orgId)
        } else {
            Organization()
        }
        with(org) {
            logger.trace("Get organization detail from database.")
            val detail = dao.getDetail()
            val hosId = detail["pcucode"] ?: ""

            // pcucode.append(hosId)

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

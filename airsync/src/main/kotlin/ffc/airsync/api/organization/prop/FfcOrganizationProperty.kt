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

package ffc.airsync.api.organization.prop

import ffc.entity.User
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

private const val TOKEN = "token"
private const val ORG_ID = "orgId"
private const val ADMIN_USER_NAME = "userOrg"
private const val ADMIN_USER_ID = "userIdOrg"
private const val DATABASE_VERSION = "dbVersion"

class FfcOrganizationProperty(private val file: File) : OrganizationProperty {

    private val property = Properties()

    init {
        require(!file.isDirectory) { "ไฟล์คอนฟิกห้ามเป็น directory" }
        if (!file.isFile) file.createNewFile()
        load()
    }

    private fun load() {
        property.load(FileInputStream(file))
    }

    override var token: String?
        get() = property.getProperty(TOKEN)
        set(value) {
            property[TOKEN] = value
            store()
        }

    private fun store() {
        property.store(FileOutputStream(file), null)
    }

    override var organizationId: String?
        get() = property.getProperty(ORG_ID)
        set(value) {
            property[ORG_ID] = value
            store()
        }
    override var adminUser: User?
        get() {
            return loadUser()
        }
        set(value) {
            property[ADMIN_USER_ID] = value?.id
            property[ADMIN_USER_NAME] = value?.name
            store()
        }

    private fun loadUser(): User? {
        property.getProperty(ADMIN_USER_NAME)?.let { name ->
            property.getProperty(ADMIN_USER_ID)?.let { id ->
                return User(id).apply { this.name = name }
            }
        }
        return null
    }

    override var databaseVersion: String?
        get() = property.getProperty(DATABASE_VERSION)
        set(value) {
            property[DATABASE_VERSION] = value
            store()
        }
}

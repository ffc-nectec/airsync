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
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import java.io.File

class FfcOrganizationPropertyTest {

    val file = File("src/test/resources/FfcOrganizationPropertyTest.cnf")
    lateinit var prop: FfcOrganizationProperty

    @Before
    fun setUp() {
        prop = FfcOrganizationProperty(file)
    }

    @Test
    fun getToken() {
        prop.token `should equal` "aa"
    }

    @Test
    fun setToken() {
        prop.token = "aa"
    }

    @Test
    fun getOrganizationId() {
        prop.organizationId `should equal` "bb"
    }

    @Test
    fun setOrganizationId() {
        prop.organizationId = "bb"
    }

    @Test
    fun setAdminUser() {
        prop.adminUser = User().apply { name = "Thanachai" }
    }

    @Test
    fun getDatabaseVersion() {
        prop.databaseVersion `should equal` "5"
    }

    @Test
    fun setDatabaseVersion() {
        prop.databaseVersion = "5"
    }
}

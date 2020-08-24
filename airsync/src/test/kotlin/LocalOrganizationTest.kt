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

import ffc.airsync.api.organization.LocalOrganization
import ffc.airsync.db.DatabaseDao
import ffc.entity.Person
import ffc.entity.Template
import ffc.entity.User
import ffc.entity.Village
import ffc.entity.healthcare.CommunityService
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit
import ffc.entity.healthcare.Icd10
import ffc.entity.healthcare.SpecialPP
import ffc.entity.place.Business
import ffc.entity.place.House
import ffc.entity.place.ReligiousPlace
import ffc.entity.place.School
import org.amshove.kluent.`should be equal to`
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class LocalOrganizationTest {

    val logConfig = "src/test/resources/propertyStoreTest.cnf"
    val propertyStore = LocalOrganization(TestDao(), logConfig)

    @After
    fun tearDown() {
        File(logConfig).delete()
    }

    @Before
    fun setUp() {
        File(logConfig).delete()
    }

    @Test
    fun setAndGetToken() {
        propertyStore.token = "abcdef"
        propertyStore.token `should be equal to` "abcdef"
    }

    @Test
    fun setAndGetOrgId() {
        propertyStore.orgId = "nectecabcdef"
        propertyStore.orgId `should be equal to` "nectecabcdef"
    }

    @Test
    fun setAndGetUser() {
        val user = User().apply { name = "nectec" }
        propertyStore.userOrg = user
        propertyStore.userOrg.name `should be equal to` "nectec"
    }
}

class TestDao : DatabaseDao {
    override fun getDetail(): HashMap<String, String> {
        return hashMapOf()
    }

    override fun getDatabaseLocaion(): File {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun init() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getUsers(): List<User> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getHouse(lookupVillage: (jVillageId: String) -> Village?, whereString: String): List<House> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getPerson(lookupDisease: (icd10: String) -> Icd10): List<Person> {
        TODO("Not yet implemented")
    }

    override fun findPerson(pcucode: String, pid: Long, lookupDisease: (icd10: String) -> Icd10): Person {
        TODO("Not yet implemented")
    }

    override fun getHealthCareService(
        lookupPatientId: (pid: String) -> String,
        lookupProviderId: (name: String) -> String,
        lookupDisease: (icd10: String) -> Icd10?,
        lookupSpecialPP: (ppCode: String) -> SpecialPP.PPType?,
        lookupServiceType: (serviceId: String) -> CommunityService.ServiceType?,
        whereString: String,
        progressCallback: (Int) -> Unit
    ): List<HealthCareService> {
        TODO("Not yet implemented")
    }

    override fun upateHouse(house: House) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun createHomeVisit(
        homeVisit: HomeVisit,
        healthCareService: HealthCareService,
        pcucode: String,
        pcucodePerson: String,
        patient: Person,
        username: String
    ): HealthCareService {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun queryMaxVisit(): Long {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getVillage(): List<Village> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getBusiness(): List<Business> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getSchool(): List<School> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getTemple(): List<ReligiousPlace> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun updateHomeVisit(
        homeVisit: HomeVisit,
        healthCareService: HealthCareService,
        pcucode: String,
        pcucodePerson: String,
        patient: Person,
        username: String
    ): HealthCareService {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getTemplate(): List<Template> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}

/*
 * Copyright (c) 2018 NECTEC
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

package ffc.airsync

import ffc.airsync.MySqlJdbi.Companion.jdbiDao
import ffc.airsync.business.QueryBusiness
import ffc.airsync.chronic.ChronicDao
import ffc.airsync.chronic.ChronicJdbi
import ffc.airsync.db.DatabaseDao
import ffc.airsync.foodshop.QueryFoodShop
import ffc.airsync.hosdetail.HosDao
import ffc.airsync.hosdetail.HosDetailJdbi
import ffc.airsync.house.HouseDao
import ffc.airsync.house.HouseJdbi
import ffc.airsync.person.PersonDao
import ffc.airsync.person.PersonJdbi
import ffc.airsync.school.QuerySchool
import ffc.airsync.template.TemplateDao
import ffc.airsync.template.TemplateJdbi
import ffc.airsync.temple.QueryTemple
import ffc.airsync.user.UserDao
import ffc.airsync.user.UserJdbi
import ffc.airsync.village.VillageDao
import ffc.airsync.village.VillageJdbi
import ffc.airsync.visit.VisitDao
import ffc.airsync.visit.VisitJdbi
import ffc.entity.Person
import ffc.entity.Template
import ffc.entity.User
import ffc.entity.Village
import ffc.entity.healthcare.Chronic
import ffc.entity.healthcare.CommunityService.ServiceType
import ffc.entity.healthcare.Disease
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit
import ffc.entity.healthcare.SpecialPP
import ffc.entity.place.Business
import ffc.entity.place.House
import ffc.entity.place.ReligiousPlace
import ffc.entity.place.School
import javax.sql.DataSource

@Deprecated("JdbiDao move to MySqlJdbi")
class JdbiDao(
    val dbHost: String = "127.0.0.1",
    val dbPort: String = "3333",
    val dbName: String = "jhcisdb",
    val dbUsername: String = "root",
    val dbPassword: String = "123456",
    var ds: DataSource? = null
) : DatabaseDao {

    val houses: HouseDao by lazy { HouseJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds) }
    val visit: VisitDao by lazy { VisitJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds) }
    val persons: PersonDao by lazy { PersonJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds) }
    val hos: HosDao by lazy { HosDetailJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds) }
    val users: UserDao by lazy { UserJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds) }
    val chronic: ChronicDao by lazy { ChronicJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds) }
    val village: VillageDao by lazy { VillageJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds) }
    val template: TemplateDao by lazy { TemplateJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds) }

    override fun getDetail(): HashMap<String, String> {
        return hos.get()
    }

    override fun getUsers(): List<User> {
        return users.get()
    }

    override fun getPerson(): List<Person> {
        val person = arrayListOf<Person>()
        person.addAll(persons.get())
        person.removeIf { it.bundle["remove"] == true }
        return person.toList()
    }

    override fun findPerson(pcucode: String, pid: Long): Person {
        return persons.find(pcucode, pid).first()
    }

    override fun getHouse(lookupVillage: (jVillageId: String) -> Village?): List<House> {
        return houses.getHouse(lookupVillage)
    }

    override fun getHouse(lookupVillage: (jVillageId: String) -> Village?, whereString: String): List<House> {
        return houses.getHouse(lookupVillage, whereString)
    }

    override fun getChronic(): List<Chronic> = chronic.get()

    override fun upateHouse(house: House) {
        houses.upateHouse(house)
    }

    override fun createHomeVisit(
        homeVisit: HomeVisit,
        healthCareService: HealthCareService,
        pcucode: String,
        pcucodePerson: String,
        patient: Person,
        username: String
    ): HealthCareService {
        return visit.createHomeVisit(homeVisit, healthCareService, pcucode, pcucodePerson, patient, username)
    }

    override fun updateHomeVisit(
        homeVisit: HomeVisit,
        healthCareService: HealthCareService,
        pcucode: String,
        pcucodePerson: String,
        patient: Person,
        username: String
    ): HealthCareService {

        return visit.updateHomeVisit(homeVisit, healthCareService, pcucode, pcucodePerson, patient, username)
    }

    override fun queryMaxVisit(): Long {
        return visit.getMaxVisit()
    }

    override fun getVillage(): List<Village> {
        return village.get()
    }

    override fun getBusiness(): List<Business> {
        val business = jdbiDao.extension<QueryBusiness, List<Business>> { get() }
        val foodShop = jdbiDao.extension<QueryFoodShop, List<Business>> { get() }

        return business + foodShop
    }

    @Deprecated("ย้ายไป Dao")
    override fun getSchool(): List<School> {
        return jdbiDao.extension<QuerySchool, List<School>> { get() }
    }

    @Deprecated("ย้ายไป Dao")
    override fun getTemple(): List<ReligiousPlace> {
        return jdbiDao.extension<QueryTemple, List<ReligiousPlace>> { get() }
    }

    override fun getHealthCareService(
        lookupPatientId: (pid: String) -> String,
        lookupProviderId: (name: String) -> String
    ): List<HealthCareService> {
        return visit.getHealthCareService(lookupPatientId, lookupProviderId)
    }

    override fun getHealthCareService(
        lookupPatientId: (pid: String) -> String,
        lookupProviderId: (name: String) -> String,
        lookupDisease: (icd10: String) -> Disease?,
        lookupSpecialPP: (ppCode: String) -> SpecialPP.PPType?,
        lookupServiceType: (serviceId: String) -> ServiceType?
    ): List<HealthCareService> {
        return visit.getHealthCareService(
            lookupPatientId,
            lookupProviderId,
            lookupDisease,
            lookupSpecialPP,
            lookupServiceType
        )
    }

    override fun getHealthCareService(
        lookupPatientId: (pid: String) -> String,
        lookupProviderId: (name: String) -> String,
        lookupDisease: (icd10: String) -> Disease?,
        lookupSpecialPP: (ppCode: String) -> SpecialPP.PPType?,
        lookupServiceType: (serviceId: String) -> ServiceType?,
        whereString: String
    ): List<HealthCareService> {
        return visit.getHealthCareService(
            lookupPatientId,
            lookupProviderId,
            lookupDisease,
            lookupSpecialPP,
            lookupServiceType,
            whereString
        )
    }

    override fun getTemplate(): List<Template> {
        return template.get()
    }
}

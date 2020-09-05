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

package ffc.airsync

import ffc.airsync.business.QueryBusiness
import ffc.airsync.chronic.NewQueryChronic
import ffc.airsync.db.DatabaseDao
import ffc.airsync.disability.DisabilityJdbi
import ffc.airsync.foodshop.QueryFoodShop
import ffc.airsync.hosdetail.HosDao
import ffc.airsync.hosdetail.HosDetailJdbi
import ffc.airsync.house.HouseDao
import ffc.airsync.house.HouseJdbi
import ffc.airsync.mysqlvariable.GetMySqlVariable
import ffc.airsync.mysqlvariable.MySqlVariableJdbi
import ffc.airsync.person.NewQueryPerson
import ffc.airsync.person.PersonDao.Lookup
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
import ffc.entity.healthcare.Disability
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit
import ffc.entity.healthcare.Icd10
import ffc.entity.healthcare.SpecialPP
import ffc.entity.place.Business
import ffc.entity.place.House
import ffc.entity.place.ReligiousPlace
import ffc.entity.place.School
import java.io.File

class JdbiDao(
    val jdbiDao: Dao = MySqlJdbi(null)
) : DatabaseDao {

    val houses: HouseDao by lazy { HouseJdbi(jdbiDao) }
    val visit: VisitDao by lazy { VisitJdbi(jdbiDao) }
    val hos: HosDao by lazy { HosDetailJdbi(jdbiDao) }
    val users: UserDao by lazy { UserJdbi(jdbiDao) }
    val village: VillageDao by lazy { VillageJdbi(jdbiDao) }
    val template: TemplateDao by lazy { TemplateJdbi(jdbiDao) }
    val configFromDb: GetMySqlVariable by lazy { MySqlVariableJdbi(jdbiDao) }

    override fun init() {
        val baseDir = getDatabaseLocaion()
        InitJhcisConfig(File(baseDir, "my.ini"), configFromDb.mysqlVersion())
    }

    override fun getDatabaseLocaion(): File {
        return File(configFromDb.mysqlLocation())
    }

    override fun getDetail(): HashMap<String, String> {
        return hos.get()
    }

    override fun getUsers(): List<User> {
        return users.get()
    }

    override fun getPerson(lookupDisease: (icd10: String) -> Icd10): List<Person> {
        val disabilityFunc = DisabilityJdbi(jdbiDao)
        val chronicFunc = NewQueryChronic(jdbiDao)
        return NewQueryPerson(jdbiDao).get {
            object : Lookup {
                override fun lookupDisease(icd10: String): Icd10 = lookupDisease(icd10)
                override fun lookupChronic(pcuCode: String, pid: String): List<Chronic> =
                    chronicFunc.getBy(pcuCode, pid) {
                        object : NewQueryChronic.Lookup {
                            override fun lookupDisease(icd10: String): Icd10 = lookupDisease(icd10)
                        }
                    }

                override fun lookupDisability(pcuCode: String, pid: String): List<Disability> =
                    disabilityFunc.get(pcuCode, pid, lookupDisease)
            }
        }
    }

    override fun findPerson(pcucode: String, pid: Long, lookupDisease: (icd10: String) -> Icd10): Person {
        val disabilityFunc = DisabilityJdbi(jdbiDao)
        val chronicFunc = NewQueryChronic(jdbiDao)
        return NewQueryPerson(jdbiDao).findBy(pcucode, pid.toString()) {
            object : Lookup {
                override fun lookupDisease(icd10: String): Icd10 = lookupDisease(icd10)
                override fun lookupChronic(pcuCode: String, pid: String): List<Chronic> =
                    chronicFunc.getBy(pcuCode, pid) {
                        object : NewQueryChronic.Lookup {
                            override fun lookupDisease(icd10: String): Icd10 = lookupDisease(icd10)
                        }
                    }

                override fun lookupDisability(pcuCode: String, pid: String): List<Disability> =
                    disabilityFunc.get(pcuCode, pid, lookupDisease)
            }
        }
    }

    override fun getHouse(lookupVillage: (jVillageId: String) -> Village?, whereString: String): List<House> {
        return houses.getHouse(lookupVillage, whereString)
    }

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
        lookupPatientId: (pcuCode: String, pid: String) -> String,
        lookupProviderId: (name: String) -> String,
        lookupDisease: (icd10: String) -> Icd10?,
        lookupSpecialPP: (ppCode: String) -> SpecialPP.PPType?,
        lookupServiceType: (serviceId: String) -> ServiceType?,
        whereString: String,
        progressCallback: (Int) -> Unit
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

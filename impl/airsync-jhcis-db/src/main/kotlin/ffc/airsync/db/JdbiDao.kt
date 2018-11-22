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

package ffc.airsync.db

import ffc.airsync.db.person.QueryPerson
import ffc.airsync.db.service.HomeVisitQuery
import ffc.airsync.db.service.NCDscreenQuery
import ffc.airsync.db.service.SpecialppQuery
import ffc.airsync.db.service.VisitDiagQuery
import ffc.airsync.db.service.VisitQuery
import ffc.airsync.db.visit.InsertData
import ffc.airsync.db.visit.InsertUpdate
import ffc.airsync.db.visit.buildInsertData
import ffc.airsync.db.visit.buildInsertDiag
import ffc.airsync.db.visit.buildInsertIndividualData
import ffc.airsync.utils.printDebug
import ffc.entity.Person
import ffc.entity.User
import ffc.entity.Village
import ffc.entity.gson.toJson
import ffc.entity.healthcare.Chronic
import ffc.entity.healthcare.CommunityService.ServiceType
import ffc.entity.healthcare.Diagnosis
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit
import ffc.entity.healthcare.Icd10
import ffc.entity.healthcare.NCDScreen
import ffc.entity.healthcare.SpecialPP
import ffc.entity.place.Business
import ffc.entity.place.House
import ffc.entity.place.ReligiousPlace
import ffc.entity.place.School
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.joda.time.LocalDate
import java.sql.Timestamp
import javax.sql.DataSource

@Deprecated("JdbiDao move to MySqlJdbi")
class JdbiDao(
    val dbHost: String = "127.0.0.1",
    val dbPort: String = "3333",
    val dbName: String = "jhcisdb",
    val dbUsername: String = "root",
    val dbPassword: String = "123456",
    val ds: DataSource? = null
) : DatabaseDao {
    companion object {
        lateinit var jdbiDao: Jdbi
    }

    init {
        jdbiDao = createJdbi()
    }

    override fun getDetail(): HashMap<String, String> {
        return jdbiDao.extension<QueryHosDetail, List<HashMap<String, String>>> { get() }[0]
    }

    override fun getUsers(): List<User> {
        return jdbiDao.extension<QueryUser, List<User>> { get() }
    }

    override fun getPerson(): List<Person> {
        return jdbiDao.extension<QueryPerson, List<Person>> { get() }
    }

    override fun findPerson(pcucode: String, pid: Long): Person {
        return jdbiDao.extension<QueryPerson, List<Person>> { findPerson(pcucode, pid) }.first()
    }

    override fun getHouse(): List<House> {
        val houses = jdbiDao.extension<QueryHouse, List<House>> { findThat() }
        houses.forEachIndexed { index, house ->
            printDebug("HouseXY = " + house.location + ", " + index)
        }
        return houses
    }

    override fun getHouse(whereString: String): List<House> {
        if (whereString.isBlank()) return arrayListOf()
        return jdbiDao.extension<QueryHouse, List<House>> { findThat(whereString) }
    }

    override fun getChronic(): List<Chronic> = jdbiDao.extension<QueryChronic, List<Chronic>> { get() }

    override fun upateHouse(house: House) {
        val houseUpdate = HouseJhcisDb(
            hid = house.identity?.id,
            road = house.road,
            xgis = house.location?.coordinates?.longitude?.toString(),
            ygis = house.location?.coordinates?.latitude?.toString(),
            hno = house.no,
            dateUpdate = Timestamp(house.timestamp.millis),

            pcucode = house.link!!.keys["pcucode"].toString(),
            hcode = house.link!!.keys["hcode"].toString().toInt()
        )
        printDebug("House update from could = ${houseUpdate.toJson()}")
        jdbiDao.extension<QueryHouse, Any> { update(houseUpdate) }
        printDebug("\tFinish upateHouse")
    }

    private fun createJdbi(): Jdbi {
        Class.forName("com.mysql.jdbc.Driver")
        val jdbi: Jdbi

        if (ds == null) {
            val ds = com.mysql.jdbc.jdbc2.optional.MysqlDataSource()
            ds.setURL("jdbc:mysql://$dbHost:$dbPort/$dbName?autoReconnect=true&useSSL=false")
            ds.databaseName = dbName
            ds.user = dbUsername
            ds.setPassword(dbPassword)
            ds.port = dbPort.toInt()
            jdbi = Jdbi.create(ds)
        } else {
            jdbi = Jdbi.create(ds)
        }

        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(SqlObjectPlugin())
        jdbi.installPlugin(KotlinSqlObjectPlugin())
        return jdbi
    }

    override fun createHomeVisit(
        homeVisit: HomeVisit,
        healthCareService: HealthCareService,
        pcucode: String,
        pcucodePerson: String,
        patient: Person,
        username: String
    ) {
        val visitNum = queryMaxVisit() + 1
        val visitData = healthCareService.buildInsertData(
            pcucode,
            visitNum,
            pcucodePerson,
            ((patient.link?.keys?.get("pid")) as String).toLong(),
            username,
            (patient.link?.keys?.get("rightcode")) as String,
            (patient.link?.keys?.get("rightno")) as String,
            (patient.link?.keys?.get("hosmain")) as String,
            (patient.link?.keys?.get("hossub")) as String
        )
        insertVisit(visitData)

        jdbiDao.extension<InsertUpdate, Unit> {
            insertVisitDiag(healthCareService.buildInsertDiag(pcucode, visitNum, username))
        }

        val visitIndividualData = homeVisit.buildInsertIndividualData(healthCareService, pcucode, visitNum, username)
        jdbiDao.extension<InsertUpdate, Unit> { insertVitsitIndividual(visitIndividualData) }
    }

    fun queryMaxVisit(): Long {
        val listMaxVisit = jdbiDao.extension<VisitQuery, List<Long>> { getMaxVisitNumber() }
        return listMaxVisit.last()
    }

    fun insertVisit(insertData: InsertData) {
        val listVisitData = arrayListOf<InsertData>().apply {
            add(insertData)
        }
        jdbiDao.extension<InsertUpdate, Unit> { insertVisit(listVisitData) }
    }

    override fun getVillage(): List<Village> {
        return jdbiDao.extension<QueryVillage, List<Village>> { get() }
    }

    override fun getBusiness(): List<Business> {
        val business = jdbiDao.extension<QueryBusiness, List<Business>> { get() }
        val foodShop = jdbiDao.extension<QueryFoodShop, List<Business>> { get() }

        return business + foodShop
    }

    override fun getSchool(): List<School> {
        return jdbiDao.extension<QuerySchool, List<School>> { get() }
    }

    override fun getTemple(): List<ReligiousPlace> {
        return jdbiDao.extension<QueryTemple, List<ReligiousPlace>> { get() }
    }

    override fun getHealthCareService(
        user: List<User>,
        person: List<Person>,
        lookupHealthType: (serviceId: String) -> ServiceType,
        lookupICD10: (icd10: String) -> Icd10,
        lookupSpecial: (specialId: String) -> SpecialPP.PPType
    ): List<HealthCareService> {

        return jdbiDao.extension<VisitQuery, List<HealthCareService>> { get() }.map { healthCare ->
            val providerId = (user.find { it.name == healthCare.providerId } ?: user.last()).id
            val patientId = person.find { it.link!!.keys["pid"] == healthCare.patientId }?.id ?: ""
            val healthcareService = copyHealthCare(providerId, patientId, healthCare)

            healthcareService.link?.keys?.get("visitno")?.toString()?.toInt()?.let { visitNumber ->
                val diagnosisIcd10 = jdbiDao.extension<VisitDiagQuery, List<Diagnosis>> { getDiag(visitNumber) }
                val specislPP = jdbiDao.extension<SpecialppQuery, List<String>> { get(visitNumber) }
                val ncdScreen = jdbiDao.extension<NCDscreenQuery, List<NCDScreen>> { get(visitNumber) }
                val homeVisit = jdbiDao.extension<HomeVisitQuery, List<HomeVisit>> { get(visitNumber) }

                healthcareService.diagnosises = diagnosisIcd10.map {
                    Diagnosis(
                        disease = lookupICD10(it.disease.id),
                        dxType = it.dxType,
                        isContinued = it.isContinued
                    )
                }.toMutableList()

                healthcareService.nextAppoint

                specislPP.forEach {
                    healthcareService.addSpecialPP(
                        lookupSpecial(it)
                    )
                }

                healthcareService.ncdScreen = ncdScreen.firstOrNull()?.let {
                    createNcdScreen(providerId, patientId, it)
                }

                homeVisit.firstOrNull()?.let { visit ->
                    visit.bundle["dateappoint"]?.let { healthcareService.nextAppoint = it as LocalDate }
                    healthcareService.communityServices.add(
                        HomeVisit(
                            serviceType = lookupHealthType(visit.serviceType.id),
                            detail = visit.detail,
                            plan = visit.plan,
                            result = visit.result
                        )
                    )
                }

            }
            healthcareService
        }
    }

    private fun createNcdScreen(
        providerId: String,
        patientId: String,
        it: NCDScreen
    ): NCDScreen {
        return NCDScreen(
            providerId = providerId,
            patientId = patientId,
            hasDmInFamily = it.hasDmInFamily,
            hasHtInFamily = it.hasHtInFamily,
            smoke = it.smoke,
            alcohol = it.alcohol,
            bloodSugar = it.bloodSugar,
            weight = it.weight,
            height = it.height,
            waist = it.waist,
            bloodPressure = it.bloodPressure,
            bloodPressure2nd = it.bloodPressure2nd
        ).apply {
            time = it.time
            link = it.link
        }
    }

    private fun copyHealthCare(
        providerId: String,
        patientId: String,
        healthCare: HealthCareService
    ): HealthCareService {
        return HealthCareService(providerId, patientId).apply {
            syntom = healthCare.syntom
            suggestion = healthCare.suggestion
            weight = healthCare.weight
            height = healthCare.height
            waist = healthCare.waist
            ass = healthCare.ass
            bloodPressure = healthCare.bloodPressure
            bloodPressure2nd = healthCare.bloodPressure2nd
            pulseRate = healthCare.pulseRate
            bodyTemperature = healthCare.bodyTemperature
            note = healthCare.note
            link = healthCare.link
        }
    }
}

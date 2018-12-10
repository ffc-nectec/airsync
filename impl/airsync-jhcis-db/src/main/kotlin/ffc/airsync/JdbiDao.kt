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

import ffc.airsync.business.QueryBusiness
import ffc.airsync.chronic.QueryChronic
import ffc.airsync.db.DatabaseDao
import ffc.airsync.disease.QueryDisease
import ffc.airsync.foodshop.QueryFoodShop
import ffc.airsync.healthtype.QueryHomeHealthType
import ffc.airsync.hosdetail.QueryHosDetail
import ffc.airsync.house.HouseJhcisDb
import ffc.airsync.house.QueryHouse
import ffc.airsync.ncds.NCDscreenQuery
import ffc.airsync.person.QueryPerson
import ffc.airsync.school.QuerySchool
import ffc.airsync.specialpp.LookupSpecialPP
import ffc.airsync.specialpp.SpecialppQuery
import ffc.airsync.temple.QueryTemple
import ffc.airsync.user.QueryUser
import ffc.airsync.utils.printDebug
import ffc.airsync.village.QueryVillage
import ffc.airsync.visit.HomeVisitQuery
import ffc.airsync.visit.InsertData
import ffc.airsync.visit.InsertUpdate
import ffc.airsync.visit.VisitDiagQuery
import ffc.airsync.visit.VisitQuery
import ffc.airsync.visit.buildInsertData
import ffc.airsync.visit.buildInsertDiag
import ffc.airsync.visit.buildInsertIndividualData
import ffc.entity.Person
import ffc.entity.User
import ffc.entity.Village
import ffc.entity.gson.toJson
import ffc.entity.healthcare.Chronic
import ffc.entity.healthcare.CommunityService.ServiceType
import ffc.entity.healthcare.Diagnosis
import ffc.entity.healthcare.Disease
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit
import ffc.entity.healthcare.NCDScreen
import ffc.entity.healthcare.SpecialPP
import ffc.entity.place.Business
import ffc.entity.place.House
import ffc.entity.place.ReligiousPlace
import ffc.entity.place.School
import ffc.entity.update
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.joda.time.LocalDate
import java.sql.Connection
import java.sql.Timestamp
import java.util.LinkedList
import java.util.Queue
import javax.sql.DataSource
import kotlin.system.measureTimeMillis

@Deprecated("JdbiDao move to MySqlJdbi")
class JdbiDao(
    val dbHost: String = "127.0.0.1",
    val dbPort: String = "3333",
    val dbName: String = "jhcisdb",
    val dbUsername: String = "root",
    val dbPassword: String = "123456",
    var ds: DataSource? = null
) : DatabaseDao {
    companion object {
        lateinit var jdbiDao: Jdbi
        val pool = arrayListOf<Connection>()
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
            dateUpdate = Timestamp(house.timestamp.plusHours(7).millis),

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
            val dsMySql = com.mysql.jdbc.jdbc2.optional.MysqlDataSource()

            dsMySql.setURL(
                "jdbc:mysql://$dbHost:$dbPort/$dbName?" +
                        "autoReconnect=true&" +
                        "useSSL=false&" +
                        "maxReconnects=2&" +
                        "autoReconnectForPools=true&" +
                        "connectTimeout=10000&" +
                        "socketTimeout=10000"
            )
            dsMySql.databaseName = dbName
            dsMySql.user = dbUsername
            dsMySql.setPassword(dbPassword)
            dsMySql.port = dbPort.toInt()
            ds = dsMySql
            // pool.add(dsMySql.connection)
            jdbi = Jdbi.create(dsMySql)
        } else {
            jdbi = Jdbi.create(ds)
        }

        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(SqlObjectPlugin())
        jdbi.installPlugin(KotlinSqlObjectPlugin())

        // createIndex { jdbi.extension<VisitQuery, Unit> { createIndex() } }
        createIndex { jdbi.extension<VisitDiagQuery, Unit> { createIndex() } }
        createIndex { jdbi.extension<SpecialppQuery, Unit> { createIndex() } }
        createIndex { jdbi.extension<NCDscreenQuery, Unit> { createIndex() } }
        createIndex { jdbi.extension<HomeVisitQuery, Unit> { createIndex() } }

        return jdbi
    }

    private fun createIndex(f: () -> Unit) {
        try {
            f()
        } catch (ignore: org.jdbi.v3.core.statement.UnableToExecuteStatementException) {
        }
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
            (patient.link?.keys?.get("rightcode")) as String?,
            (patient.link?.keys?.get("rightno")) as String?,
            (patient.link?.keys?.get("hosmain")) as String?,
            (patient.link?.keys?.get("hossub")) as String?
        )
        insertVisit(visitData)

        val insertDiagData = healthCareService.buildInsertDiag(pcucode, visitNum, username)
        jdbiDao.extension<InsertUpdate, Unit> {
            insertVisitDiag(insertDiagData)
        }

        val visitIndividualData = homeVisit.buildInsertIndividualData(healthCareService, pcucode, visitNum, username)
        jdbiDao.extension<InsertUpdate, Unit> { insertVitsitIndividual(visitIndividualData) }
    }

    override fun queryMaxVisit(): Long {
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
        lookupPatientId: (pid: String) -> String,
        lookupProviderId: (name: String) -> String
    ): List<HealthCareService> {
        return getHealthCareService(
            lookupPatientId, lookupProviderId,
            lookupDisease = { icd10 ->
                jdbiDao.extension<QueryDisease, List<Disease>> {
                    get(icd10)
                }.firstOrNull()
            },
            lookupServiceType = { serviceId ->
                jdbiDao.extension<QueryHomeHealthType, List<ServiceType>> {
                    get(serviceId)
                }.firstOrNull()
            },
            lookupSpecialPP = { ppCode ->
                jdbiDao.extension<LookupSpecialPP, List<SpecialPP.PPType>> { get(ppCode) }.firstOrNull()
            }
        )
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
        return HealthCareService(providerId, patientId).update(healthCare.timestamp) {
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
            time = healthCare.time
            endTime = healthCare.endTime
        }
    }

    override fun getHealthCareService(
        lookupPatientId: (pid: String) -> String,
        lookupProviderId: (name: String) -> String,
        lookupDisease: (icd10: String) -> Disease?,
        lookupSpecialPP: (ppCode: String) -> SpecialPP.PPType?,
        lookupServiceType: (serviceId: String) -> ServiceType?
    ): List<HealthCareService> {
        return getHealthCareService(
            lookupPatientId,
            lookupProviderId,
            lookupDisease,
            lookupSpecialPP,
            lookupServiceType,
            ""
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
        var i = 0
        val result = if (whereString.isBlank())
            jdbiDao.extension<VisitQuery, List<HealthCareService>> { get() }
        else
            jdbiDao.extension<VisitQuery, List<HealthCareService>> { get(whereString) }
        val size = result.size
        val avgTimeRun: Queue<Long> = LinkedList()
        var sumTime = 0L

        val specialPpList = hashMapOf<Long, List<String>>()
        val ncdScreenList = hashMapOf<Long, List<NCDScreen>>()
        val homeVisitList = hashMapOf<Long, List<HomeVisit>>()

        jdbiDao.extension<SpecialppQuery, List<Map<Long, String>>> { getAll() }.forEach {
            mapList(it, specialPpList)
        }
        jdbiDao.extension<NCDscreenQuery, List<Map<Long, NCDScreen>>> { getAll() }.forEach {
            mapList(it, ncdScreenList)
        }

        jdbiDao.extension<HomeVisitQuery, List<Map<Long, HomeVisit>>> { getAll() }.forEach {
            mapList(it, homeVisitList)
        }

        return result.map { healthCare ->
            var healthcareService = HealthCareService("", "")

            val allRunTime = measureTimeMillis {

                i++
                var providerId = ""
                var patientId = ""
                val runtimeLookupUser = measureTimeMillis {
                    providerId = lookupProviderId(healthCare.providerId)
                    patientId = lookupPatientId(healthCare.patientId)
                }

                healthcareService = copyHealthCare(providerId, patientId, healthCare)
                healthcareService.link?.keys?.get("visitno")?.toString()?.toLong()?.let { visitNumber ->

                    var diagnosisIcd10: List<Diagnosis> = emptyList()
                    var specislPP: List<String> = emptyList()
                    var ncdScreen: List<NCDScreen> = emptyList()
                    var homeVisit: List<HomeVisit> = emptyList()

                    val runtimeQueryDb = measureTimeMillis {
                        diagnosisIcd10 = jdbiDao.extension<VisitDiagQuery, List<Diagnosis>> { getDiag(visitNumber) }
                        specislPP = specialPpList[visitNumber] ?: emptyList()

                        ncdScreen = ncdScreenList[visitNumber] ?: emptyList()
                        homeVisit = homeVisitList[visitNumber] ?: emptyList()
                    }

                    val runtimeLookupApi = measureTimeMillis {
                        healthcareService.diagnosises = diagnosisIcd10.map {
                            Diagnosis(
                                disease = lookupDisease(it.disease.id.trim()) ?: it.disease,
                                dxType = it.dxType,
                                isContinued = it.isContinued
                            )
                        }.toMutableList()

                        healthcareService.nextAppoint

                        specislPP.forEach {
                            healthcareService.addSpecialPP(
                                lookupSpecialPP(it.trim()) ?: SpecialPP.PPType("it", "it")
                            )
                        }

                        healthcareService.ncdScreen = ncdScreen.firstOrNull()?.let {
                            createNcdScreen(providerId, patientId, it)
                        }

                        homeVisit.firstOrNull()?.let { visit ->
                            visit.bundle["dateappoint"]?.let { healthcareService.nextAppoint = it as LocalDate }
                            healthcareService.communityServices.add(
                                HomeVisit(
                                    serviceType = lookupServiceType(visit.serviceType.id.trim()) ?: visit.serviceType,
                                    detail = visit.detail,
                                    plan = visit.plan,
                                    result = visit.result
                                )
                            )
                        }
                    }
                    if (i % 300 == 0 || i == size) {
                        print("Visit $i:$size")
                        print("\tLookupUser:$runtimeLookupUser")
                        print("\tRuntime DB:$runtimeQueryDb")
                        print("\tLookupApi:$runtimeLookupApi")
                    }
                }
            }

            if (avgTimeRun.size > 10000)
                sumTime -= avgTimeRun.poll()
            avgTimeRun.offer(allRunTime)
            sumTime += allRunTime

            val avgTime = sumTime / avgTimeRun.size

            if (i % 300 == 0 || i == size) {
                ((size - i) * avgTime).printTime()
                println()
            }
            healthcareService
        }
    }

    private inline fun <reified T> mapList(
        it: Map<Long, T>,
        resut: HashMap<Long, List<T>>
    ) {
        val toList = it.toList()
        val key = toList[0].first
        val value = toList[0].second
        if (resut[key] == null || resut[key]?.isEmpty() == true)
            resut[key] = listOf(value)
        else {
            resut[key] =
                    listOf(*resut[key]!!.toTypedArray(), value)
        }
    }

    private fun calAvgTime(avgTimeRun: Queue<Long>): Long {
        var sum = 0L
        avgTimeRun.forEach { sum += it }

        return sum / avgTimeRun.size
    }

    fun Long.printTime() {
        if (this > 0) {
            val sec = (this / 1000) % 60
            val min = (this / 60000) % 60
            val hour = (this / 36e5).toInt()
            print("\t$hour:$min:$sec")
        }
    }
}

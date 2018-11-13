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

import ffc.airsync.db.visit.InsertData
import ffc.airsync.db.visit.InsertUpdate
import ffc.airsync.db.visit.Query
import ffc.airsync.db.visit.VisitUtil
import ffc.airsync.db.visit.buildInsertData
import ffc.airsync.db.visit.buildInsertDiag
import ffc.airsync.db.visit.buildInsertIndividualData
import ffc.airsync.utils.printDebug
import ffc.entity.Person
import ffc.entity.User
import ffc.entity.Village
import ffc.entity.gson.toJson
import ffc.entity.healthcare.Chronic
import ffc.entity.healthcare.CommunityServiceType
import ffc.entity.healthcare.Disease
import ffc.entity.healthcare.HomeVisit
import ffc.entity.place.Business
import ffc.entity.place.House
import ffc.entity.place.ReligiousPlace
import ffc.entity.place.School
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import java.sql.Timestamp
import javax.sql.DataSource

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
        pcucode: String,
        pcucodePerson: String,
        patient: Person,
        username: String
    ) {
        val visitNum = queryMaxVisit() + 1
        val visitData = homeVisit.buildInsertData(
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
            insertVisitDiag(homeVisit.buildInsertDiag(pcucode, visitNum, username))
        }

        val visitIndividualData = homeVisit.buildInsertIndividualData(pcucode, visitNum, username)
        jdbiDao.extension<InsertUpdate, Unit> { insertVitsitIndividual(visitIndividualData) }
    }

    fun queryMaxVisit(): Long {
        val listMaxVisit = jdbiDao.extension<Query, List<Long>> { getMaxVisitNumber() }
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

    override fun getHomeVisit(
        user: List<User>,
        person: List<Person>,
        lookupDisease: (icd10: String) -> Disease,
        lookupHealthType: (id: String) -> CommunityServiceType
    ): List<HomeVisit> {
        val homeVisitList = arrayListOf<HomeVisit>()

        val visit = VisitUtil()

        jdbiDao.extension<Query, List<HomeVisit>> { getHomeVisit() }.forEach { current ->

            visit.`ใสข้อมูล Disease`(current, lookupDisease)

            val oldHomeVisit = homeVisitList.find { visit.checkDuplicateVisitDiag(it, current) }

            if (oldHomeVisit != null) { // มีข้อมูลการ visit ซ้ำเกิดการการมีหลาย diagnosises
                oldHomeVisit.diagnosises.add(current.diagnosises.first())
            } else {
                val homeVisit = visit.mapId(user, current, person)
                homeVisitList.add(homeVisit)
            }
        }
        return homeVisitList
    }
}

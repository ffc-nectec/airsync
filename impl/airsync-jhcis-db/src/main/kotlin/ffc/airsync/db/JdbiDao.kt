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

import ffc.airsync.utils.printDebug
import ffc.entity.House
import ffc.entity.Person
import ffc.entity.User
import ffc.entity.gson.toJson
import ffc.entity.healthcare.Chronic
import ffc.entity.healthcare.HomeVisit
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

            pcuCode = house.link!!.keys["pcuCode"].toString(),
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
        val visitData = VisitData(
            homeVisit,
            pcucode,
            visitNum,
            pcucodePerson,
            (patient.link!!.keys["pid"] as String).toLong(),
            username,
            patient.bundle["rightcode"] as String,
            patient.bundle["rightno"] as String,
            patient.bundle["hosmain"] as String,
            patient.bundle["hossub"] as String

        )
        insertVisit(visitData)

        val visitDiagData = VisitDiagData(
            homeVisit,
            pcucode,
            visitNum,
            username
        )
        jdbiDao.extension<QueryVisit, Unit> { insertVisitDiag(visitDiagData.sqlData) }

        val visitIndividualData = VisitIndividualData(homeVisit, pcucode, visitNum, username)
        jdbiDao.extension<QueryVisit, Unit> { insertVitsitIndividual(visitIndividualData) }
    }

    fun queryMaxVisit(): Long {
        val listMaxVisit = jdbiDao.extension<QueryVisit, List<Long>> { getMaxVisitNumber() }
        return listMaxVisit.last()
    }

    fun insertVisit(visitData: VisitData) {
        val visitData = arrayListOf<VisitData>().apply {
            add(visitData)
        }
        jdbiDao.extension<QueryVisit, Unit> { insertVisit(visitData) }
    }
}

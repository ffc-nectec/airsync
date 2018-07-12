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
import ffc.entity.Chronic
import ffc.entity.House
import ffc.entity.Person
import ffc.entity.User
import ffc.entity.gson.toJson
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import java.sql.Timestamp

class JdbiDao(
    val dbHost: String,
    val dbPort: String,
    val dbName: String,
    val dbUsername: String,
    val dbPassword: String
) : DatabaseDao {

    override fun getDetail(): HashMap<String, String> {
        return createJdbi().extension<QueryHosDetail, List<HashMap<String, String>>> { get() }[0]
    }

    override fun getUsers(): List<User> {
        return createJdbi().extension<QueryUser, List<User>> { get() }
    }

    override fun getPerson(): List<Person> {
        return createJdbi().extension<QueryPerson, List<Person>> { get() }
    }

    override fun getHouse(): List<House> {
        val houses = createJdbi().extension<QueryHouse, List<House>> { get() }
        houses.forEachIndexed { index, house ->
            printDebug("HouseXY = " + house.location + ", " + index)
        }
        return houses
    }

    override fun getChronic(): List<Chronic> = createJdbi().extension<QueryChronic, List<Chronic>> { get() }

    override fun upateHouse(house: House) {
        val houseUpdate = HouseJhcisDb(
                hid = house.identity?.id ?: "",
                road = house.road ?: "",
                xgis = house.location?.coordinates?.longitude.toString(),
                ygis = house.location?.coordinates?.latitude.toString(),
                hno = house.no ?: "",
                dateUpdate = Timestamp(house.timestamp.millis),

                pcuCode = house.link!!.keys["pcucode"].toString(),
                hcode = house.link!!.keys["hcode"].toString().toInt()
        )
        printDebug("House update from could = ${houseUpdate.toJson()}")
        createJdbi().extension<QueryHouse, Any> { update(houseUpdate) }

        /*val querySql = """
UPDATE `house`
  SET
   `hid`=?,
   `road`=?,
   `xgis`=?,
   `ygis`=?,
   `hno`=?,
   `dateupdate`=?
WHERE  `pcucode`=? AND `hcode`=?;
    """

        printDebug("upateHouse")
        printDebug("\tGet value ${house.timestamp}")
        val jdbi = createJdbi()
        jdbi.withHandle<Any, Exception> {
            it.execute(querySql,
                    house.identity?.id,
                    house.no,
                    house.road,
                    house.location?.coordinates?.longitude,
                    house.location?.coordinates?.latitude,
                    Timestamp(house.timestamp.millis),
                    house.link!!.keys["pcucode"],
                    house.link!!.keys["hid"]
            )
        }*/
        printDebug("\tFinish upateHouse")
    }

    private fun createJdbi(): Jdbi {
        Class.forName("com.mysql.jdbc.Driver")

        val ds = com.mysql.jdbc.jdbc2.optional.MysqlDataSource()
        ds.setURL("jdbc:mysql://$dbHost:$dbPort/$dbName?autoReconnect=true&useSSL=false")
        ds.databaseName = dbName
        ds.user = dbUsername
        ds.setPassword(dbPassword)
        ds.port = dbPort.toInt()

        val jdbi = Jdbi.create(ds)
        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(SqlObjectPlugin())
        jdbi.installPlugin(KotlinSqlObjectPlugin())
        return jdbi
    }
}

/*
 * Copyright (c) 2561 NECTEC
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
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import java.sql.Timestamp

class JdbiDatabaseDao(
    val dbHost: String,
    val dbPort: String,
    val dbName: String,
    val dbUsername: String,
    val dbPassword: String
) : DatabaseDao {

    override fun getPerson(): List<Person> {
        return createJdbi().extension<QueryPerson, List<Person>> { getPerson() }
    }

    override fun getHouse(): List<House> {
        val houses = createJdbi().extension<QueryHouse, List<House>> { getHouse() }
        houses.forEachIndexed { index, house ->
            printDebug("HouseXY = " + house.location + ", " + index)
        }
        return houses
    }

    override fun getChronic(): List<Chronic> = createJdbi().extension<QueryChronic, List<Chronic>> { getChronic() }

    override fun upateHouse(house: House) {

        val querySql = """
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
        }
        printDebug("\tFinish upateHouse")
    }

    private fun createJdbi(): Jdbi {
        Class.forName("com.mysql.jdbc.Driver")

        val ds = com.mysql.jdbc.jdbc2.optional.MysqlDataSource()
        ds.setURL("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?autoReconnect=true&useSSL=false")
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


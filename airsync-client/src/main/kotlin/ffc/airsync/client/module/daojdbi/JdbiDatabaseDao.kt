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

package ffc.airsync.client.module.daojdbi

import ffc.model.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.extension.ExtensionCallback
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import java.sql.Timestamp
import java.time.LocalDate


class JdbiDatabaseDao(val dbHost: String, val dbPort: String, val dbName: String, val dbUsername: String, val dbPassword: String) : DatabaseDao {


    override fun getPerson(): List<Person> {

        val jdbi = createJdbi()

        return jdbi.withExtension<List<Person>, QueryPerson, Exception>(QueryPerson::class.java, ExtensionCallback {
            it.getPerson()
        })
    }

    override fun getHouse(): List<Address> {
        val jdbi = createJdbi()

        val resultHouse = jdbi.withExtension<List<Address>, QueryHouse, Exception>(QueryHouse::class.java, ExtensionCallback {
            it.getHouse()
        })


        var i = 0
        resultHouse.forEach {
            printDebug("House= " + it.changwat + " XY = " + it.coordinates + ", " + i++)
        }
        return resultHouse

    }

    override fun getChronic(): List<Chronic> {
        val jdbi = createJdbi()
        val resultChronic = jdbi.withExtension<List<Chronic>, QueryChronic, Exception>(QueryChronic::class.java, ExtensionCallback {
            it.getChronic()
        })

        return resultChronic

    }


    override fun upateHouse(house: Address) {

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
        printDebug("\tGet value ${house.dateUpdate.toDateTime().toString()}")
        val jdbi = createJdbi()
        jdbi.withHandle<Any, Exception> {
            it.execute(querySql,
              house.identity?.id,
              house.road,
              house.coordinates?.longitude,
              house.coordinates?.latitude,
              house.no,
              Timestamp(house.dateUpdate.millis),
              house.pcuCode,
              house.hid
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


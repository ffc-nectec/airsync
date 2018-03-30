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

package ffc.airsync.client.client.module.daojdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.extension.ExtensionCallback
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin


class JdbiDatabaseDao : DatabaseDao {
    override fun getPerson(): List<Person> {

        val jdbi = createJdbi()

        return jdbi.withExtension<List<Person>,QueryPerson,Exception>(QueryPerson::class.java, ExtensionCallback {
            it.getPerson()
        })
    }

    override fun getHouse(): List<House> {
        val jdbi = createJdbi()

        val resultHouse = jdbi.withExtension<List<House>,QueryHouse,Exception>(QueryHouse::class.java, ExtensionCallback {
            it.getHouse()
        })


        var i=0
        resultHouse.forEach {
            println("House= "+it.houseId+" XY = "+it.xgis+", "+it.ygis+" Chronic = "+it.haveChronics+" "+i++)
        }




        return resultHouse

    }


    private fun createJdbi() :Jdbi{
        Class.forName("com.mysql.jdbc.Driver")


        val ds = com.mysql.jdbc.jdbc2.optional.MysqlDataSource()
        ds.setURL("jdbc:mysql://127.0.0.1:3333/jhcisdb" + "?autoReconnect=true&useSSL=false")
        ds.databaseName="jhcisdb"
        ds.user = "root"
        ds.setPassword("123456")
        ds.port=3333


        val jdbi = Jdbi.create(ds)
        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(SqlObjectPlugin())
        jdbi.installPlugin(KotlinSqlObjectPlugin())

        return jdbi

    }
}


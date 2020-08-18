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

package ffc.airsync.house.newlib

import ffc.airsync.MySqlUnitTestServer
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.junit.Ignore
import org.junit.Test
import java.sql.DriverManager

@Ignore("สำหรับทดสอบฐานจริง")
class HouseExposedSqlTest {

    @Test
    fun getAllHouse() {
        val mysql = MySqlUnitTestServer("jhcis", "db/house.sql")
        val dao = HouseExposedSql {
            DriverManager.getConnection("jdbc:mysql://localhost:32312/jhcis", "root1", "123456")
        }

        val house = dao.getAllHouse()

        val house1 = house.find { it.no == "2" }!!
        house1.road!! `should be equal to` "สม"
        house1.location!!.coordinates.longitude `should be equal to` 100.02421170473099
        house1.location!!.coordinates.latitude `should be equal to` 13.509755642447844
        house1.link!!.keys["hcode"] `should be` 1
        house1.link!!.keys["pcucode"].toString() `should be equal to` "07918"
        house1.link!!.keys["villcode"].toString() `should be equal to` "89876789"
        house1.link!!.keys["pcucodepersonvola"].toString() `should be equal to` "07854"
        house1.link!!.keys["pidvola"] `should be` 32

        val house2 = house.find { it.no == "3" }!!
        house2.road!! `should be equal to` "ใจ"
        house2.location!!.coordinates.longitude `should be equal to` 100.02421170473090
        house2.location!!.coordinates.latitude `should be equal to` 13.509755642447845
        house2.link!!.keys["hcode"] `should be` 2
        house2.link!!.keys["pcucode"].toString() `should be equal to` "04933"
        house2.link!!.keys["villcode"].toString() `should be equal to` "83743023"
        house2.link!!.keys["pcucodepersonvola"].toString() `should be equal to` "9382"
        house2.link!!.keys["pidvola"] `should be` 43

        mysql.stop()
    }
}

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

package ffc.airsync.api.dao

import com.google.gson.Gson
import com.google.gson.JsonParser
import ffc.model.Address
import org.junit.Test
import java.util.*
import com.mongodb.BasicDBObject
import ffc.model.fromJson
import ffc.model.printDebug
import com.google.gson.JsonObject
import ffc.model.Identity
import me.piruin.geok.LatLng


class mongoTest {


    fun insertData() {
        val mongoHouseDao = MongoHouseDao("127.0.0.1", 27017, "ffc", "house")
        val house = Address(44)
        house.tambon = "เขาจันทร์ วาส"
        house.hid = 1

        val house2 = Address(48)
        house2.tambon = "เขาขาด"
        house2.hid = 2

        mongoHouseDao.insert(UUID.fromString("f247ead5-6383-5e74-2d9e-8ee1f50542be"), house)
        mongoHouseDao.insert(UUID.fromString("f247ead5-6383-5e74-2d9e-8ee1f50542be"), house2)

    }


    fun queryData() {
        val mongoHouseDao = MongoHouseDao("127.0.0.1", 27017, "ffc", "house")

        val houseStore = mongoHouseDao.findByHouseId(UUID.fromString("f247ead5-6383-5e74-2d9e-8ee1f50542be"), 2)
        println(houseStore)
        println(houseStore!!.data.tambon)

    }


    fun queryOrgUuid() {
        val mongoHouseDao = MongoHouseDao("127.0.0.1", 27017, "ffc", "house")
        val listHouse = mongoHouseDao.find(UUID.fromString("f247ead5-6383-5e74-2d9e-8ee1f50542be"))
        listHouse.forEach {
            println(it.data.tambon)
        }
    }


    fun convertJsonTest() {

        val data = """
            {
  "identity": {
    "type": "thailand-household-id",
    "id": "70050034545"
  },
  "type": "House",
  "no": "3",
  "hid": 2,
  "id": 4234228966935819000
}
        """.trimIndent()
        printDebug(data)

        val house: Address = data.fromJson()
        printDebug(house)
    }
}

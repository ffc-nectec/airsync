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

package ffc.airsync.api.user.sync

import ffc.airsync.api.user.sync.UserDataStatus.CREATE
import ffc.airsync.api.user.sync.UserDataStatus.EQUAL
import ffc.airsync.api.user.sync.UserDataStatus.UPDATE
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.User
import ffc.entity.copy
import ffc.entity.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import max212.kotlin.util.hash.SHA265
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`
import org.junit.Test

class UtilKtTest {
    val idCard1 = SHA265().hash("2315455487952")
    val user1 = createUser("สมหมาย", idCard = idCard1)

    val idCard2 = SHA265().hash("3215821320124")
    val user2 = createUser("สมปอง", idCard = idCard2)

    val idCard3 = SHA265().hash("1973125468012")
    val user3 = createUser("สมใจ", idCard = idCard3)

    val idCard4 = SHA265().hash("6582468793214")
    val user4 = createUser("สมฤดี", idCard = idCard4)

    val userList = arrayListOf(user1, user2, user3, user4)

    @Test
    fun eqFalse() {
        user1.eq(user2) `should be equal to` false
        user2.eq(user1) `should be equal to` false
    }

    @Test
    fun eqWithName() {
        val compare = createUser("สมปอง")

        compare.eq(user2) `should be equal to` true
        user2.eq(compare) `should be equal to` true
    }

    @Test
    fun eqWithIdCard() {
        // Change username
        createUser("sompong", idCard = idCard2).eq(user2) `should be equal to` true
    }

    @Test
    fun getIdCareHaveValue() {
        user1.getIdCard()!! `should be equal to` idCard1
    }

    @Test
    fun getIdCareNullVale() {
        val case1 = User("ปานนคร")
        val case2 = User("สำเพ็ง").apply {
            link = Link(System.JHICS)
            link!!.keys["testkey"] = "testValue"
        }

        case1.getIdCard() `should be` null
        case2.getIdCard() `should be` null
    }

    @Test
    fun mapUserOneWithTwoAll() {
        runBlocking {
            delay(100)
            val user1New = user1.copy().update { }
            val user2New = user2.copy()
            val user3New = user3.copy()
            val user4New = user4.copy().update { name = "ใจกล้า" }

            val userList2 = arrayListOf(user1New, user2New, user3New, user4New)

            val result = mapUserOneWithTwo(userList2, userList)

            // Test map object
            result.forEach {
                it.first.eq(it.second!!) `should be equal to` true
            }

            // Test status update
            result.find { it.first.name == user1New.name }!!.third `should be` UPDATE
            result.find { it.first.name == user2New.name }!!.third `should be` EQUAL
            result.find { it.first.name == user3New.name }!!.third `should be` EQUAL
            result.find { it.first.name == user4New.name }!!.third `should be` UPDATE
        }
    }

    @Test
    fun mapUserOneWithTwoHaveNull() {
        runBlocking {
            delay(100)
            val user1New = user1.copy().update { }
            val user2New = user2.copy()
            val user3New = user3.copy()
            val user4New = user4.copy().update { name = "ใจกล้า" }
            val user5New = createUser("ธนชัย")
            val user6New = createUser("ทองคำ")

            val userList2 = arrayListOf(user1New, user2New, user3New, user4New, user5New, user6New)

            val result = mapUserOneWithTwo(userList2, userList)

            val result1 = result.find { it.first.name == user1New.name }
            val result2 = result.find { it.first.name == user2New.name }
            val result3 = result.find { it.first.name == user3New.name }
            val result4 = result.find { it.first.name == user4New.name }
            val result5 = result.find { it.first.name == user5New.name }
            val result6 = result.find { it.first.name == user6New.name }

            // Test map object
            result1!!.first.eq(result1.second!!) `should be equal to` true
            result2!!.first.eq(result2.second!!) `should be equal to` true
            result3!!.first.eq(result3.second!!) `should be equal to` true
            result4!!.first.eq(result4.second!!) `should be equal to` true
            result5!!.second `should equal` null
            result6!!.second `should equal` null

            // Test status update
            result1.third `should be` UPDATE
            result2.third `should be` EQUAL
            result3.third `should be` EQUAL
            result4.third `should be` UPDATE
            result5.third `should be` CREATE
            result6.third `should be` CREATE
        }
    }

    fun createUser(name: String, role: User.Role = User.Role.PROVIDER, idCard: String? = null): User =
        User().apply {
            this.name = name
            password = "catbite"
            this.roles.add(role)
            idCard?.let {
                link = Link(System.JHICS)
                link!!.keys["idcard"] = idCard
            }
        }
}

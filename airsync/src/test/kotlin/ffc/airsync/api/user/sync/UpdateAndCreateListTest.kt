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
import org.amshove.kluent.`should not be equal to`
import org.amshove.kluent.`should not be`
import org.junit.Test

class UpdateAndCreateListTest {
    val idCard1 = SHA265().hash("2315455487952")
    val user1 = createUser("สมหมาย", idCard = idCard1)
    val idCard2 = SHA265().hash("3215821320124")
    val user2 = createUser("สมปอง", idCard = idCard2)
    val idCard3 = SHA265().hash("1973125468012")
    val user3 = createUser("สมใจ", idCard = idCard3)
    val idCard4 = SHA265().hash("6582468793214")
    val user4 = createUser("สมฤดี", idCard = idCard4)
    val userList = arrayListOf(user1, user2, user3, user4)

    lateinit var user1New: User
    lateinit var user2New: User
    lateinit var user3New: User
    lateinit var user4New: User
    lateinit var user5New: User
    lateinit var user6New: User

    init {
        runBlocking {
            delay(100)
            user1New = user1.copy().update { }
            user2New = user2.copy()
            user3New = user3.copy()
            user4New = user4.copy().update { name = "ใจกล้า" }
            user5New = createUser("ธนชัย", idCard = SHA265().hash("3215455214457"))
            user6New = createUser("ทองคำ", idCard = SHA265().hash("2132144512478"))
        }
    }

    @Test
    fun getListUpdateCase() {
        val userList2 = arrayListOf(user1New, user2New, user3New, user4New)

        val (update, create) = UpdateAndCreateList().getList(userList2, userList)

        create.isEmpty() `should be equal to` true

        val result1 = update.find { it.name == user1New.name }
        val result2 = update.find { it.name == user2New.name }
        val result3 = update.find { it.name == user3New.name }
        val result4 = update.find { it.name == user4New.name }

        result1!!.id `should be equal to` user1.id
        result2 `should be` null
        result3 `should be` null
        result4!!.id `should be equal to` user4.id
        result4.name `should not be equal to` user4.name // มีการอัพชื่อ ต้องไม่เหมือนเดิม
    }

    @Test
    fun getListCreateCase() {
        val userList2 = arrayListOf(user1New, user2New, user3New, user4New, user5New, user6New)
        val (update, create) = UpdateAndCreateList().getList(userList2, userList)

        update.size `should be equal to` 2
        create.size `should be equal to` 2

        create.find { it.name == user5New.name } `should not be` null
        create.find { it.name == user6New.name } `should not be` null
    }

    @Test
    fun mapUser() {
        val userList2 = arrayListOf(user1New, user2New, user3New, user4New, user5New, user6New)
        val (_, _, allMap) = UpdateAndCreateList().getList(userList2, userList)

        allMap.size `should be equal to` 4

        val result1 = allMap.find { it.name == user1New.name }!!
        val result2 = allMap.find { it.name == user2New.name }!!
        val result3 = allMap.find { it.name == user3New.name }!!
        val result4 = allMap.find { it.name == user4New.name }!!

        result1.id `should be equal to` user1.id
        result2.id `should be equal to` user2.id
        result3.id `should be equal to` user3.id
        result4.id `should be equal to` user4.id
    }

    private fun createUser(name: String, role: User.Role = User.Role.PROVIDER, idCard: String? = null): User =
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

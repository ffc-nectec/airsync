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

package ffc.airsync.api.pidvola

import ffc.entity.Link
import ffc.entity.Person
import ffc.entity.System.JHICS
import ffc.entity.ThaiCitizenId
import ffc.entity.User
import ffc.entity.User.Role.PROVIDER
import ffc.entity.User.Role.SURVEYOR
import ffc.entity.place.House
import max212.kotlin.util.hash.SHA265
import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test

class VolaProcessV1Test {
    val originalUserList = arrayListOf<User>()
    val originalHouseList = arrayListOf<House>()
    val originalPersonList = arrayListOf<Person>()

    val proc = VolaProcessV1()

    @Test
    fun processUser() {
        val test = proc.processUser(originalUserList, originalPersonList)
        test.size `should be equal to` 2
        test.find { it.name == "อสม. สมนึก" }!!.bundle["pid"].toString() `should be equal to` "1"
        test.find { it.name == "อสม. สมนึก" }!!.link!!.keys["pcucode"].toString() `should be equal to` "9999"
        test.find { it.name == "อสม. สมใจ" }!!.bundle["pid"].toString() `should be equal to` "2"
        test.find { it.name == "อสม. สมใจ" }!!.link!!.keys["pcucode"].toString() `should be equal to` "9999"
    }

    @Test
    fun processHouse() {
        val user = proc.processUser(originalUserList, originalPersonList)
        val test = proc.processHouse(originalHouseList, user)

        val user1 = user.find { it.name == "อสม. สมนึก" }!!
        val user2 = user.find { it.name == "อสม. สมใจ" }!!

        test.find { it.no == "101" }!!.allowUserId.contains(user1.id) `should be equal to` true
        test.find { it.no == "111" }!!.allowUserId.contains(user1.id) `should be equal to` true
        test.find { it.no == "102" }!!.allowUserId.contains(user2.id) `should be equal to` true
        test.find { it.no == "122" }!!.allowUserId.contains(user2.id) `should be equal to` true
        test.size `should be equal to` 4
    }

    @Before
    fun setUp() {
        val sha265 = SHA265()
        // Setup user
        originalUserList.add(User().apply {
            // pid = 1
            name = "อสม. สมนึก"
            roles.add(SURVEYOR)
            link = Link(JHICS).apply {
                keys["pcucode"] = "9999"
                keys["idcard"] = sha265.hash("8888877777661")
            }
        })
        originalUserList.add(User().apply {
            // pid = 2
            name = "อสม. สมใจ"
            roles.add(SURVEYOR)
            link = Link(JHICS).apply {
                keys["pcucode"] = "9999"
                keys["idcard"] = sha265.hash("8888877777662")
            }
        })
        originalUserList.add(User().apply {
            name = "เจ้าหน้าที่"
            roles.add(PROVIDER)
            link = Link(JHICS).apply {
                keys["pcucode"] = "9999"
                keys["idcard"] = sha265.hash("8888877777663")
            }
        })
        // Test bug
        originalUserList.add(User().apply {
            name = "เจ้าหน้าที่ Bug1"
            roles.add(PROVIDER)
        })

        originalUserList.add(User().apply {
            name = "เจ้าหน้าที่ Bug2"
            roles.add(PROVIDER)
            link = Link(JHICS)
        })

        // Setup Person
        originalPersonList.add(Person(prename = "อสม. สมนึก").apply {
            identities.add(ThaiCitizenId("8888877777661"))
            link = Link(JHICS).apply {
                keys["pid"] = "1"
                keys["pcucodeperson"] = "9999"
            }
        })
        originalPersonList.add(Person(prename = "อสม. สมใจ").apply {
            identities.add(ThaiCitizenId("8888877777662"))
            link = Link(JHICS).apply {
                keys["pid"] = "2"
                keys["pcucodeperson"] = "9999"
            }
        })
        originalPersonList.add(Person(prename = "Dummy").apply {
            identities.add(ThaiCitizenId("8888877777999"))
            link = Link(JHICS).apply {
                keys["pid"] = "9"
                keys["pcucodeperson"] = "9999"
            }
        })

        // Setup House
        originalHouseList.add(House().apply {
            no = "101"
            link = Link(JHICS).apply {
                keys["pcucode"] = "9999"
                keys["pcucodepersonvola"] = "9999"
                keys["pidvola"] = "1"
            }
        })
        originalHouseList.add(House().apply {
            no = "111"
            link = Link(JHICS).apply {
                keys["pcucode"] = "9999"
                keys["pcucodepersonvola"] = "9999"
                keys["pidvola"] = "1"
            }
        })
        originalHouseList.add(House().apply {
            no = "102"
            link = Link(JHICS).apply {
                keys["pcucode"] = "9999"
                keys["pcucodepersonvola"] = "9999"
                keys["pidvola"] = "2"
            }
        })
        originalHouseList.add(House().apply {
            no = "122"
            link = Link(JHICS).apply {
                keys["pcucode"] = "9999"
                keys["pcucodepersonvola"] = "9999"
                keys["pidvola"] = "2"
            }
        })
        originalHouseList.add(House().apply {
            no = "888"
            link = Link(JHICS).apply {
                keys["pcucode"] = "9999"
            }
        })
    }
}

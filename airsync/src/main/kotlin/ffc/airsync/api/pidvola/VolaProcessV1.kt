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

import ffc.airsync.utils.getLogger
import ffc.entity.Person
import ffc.entity.User
import ffc.entity.copy
import ffc.entity.place.House
import max212.kotlin.util.hash.SHA265

/**
 * ตอนนี้ยังไม่ได้ประมวลผลถึงคน
 * ตอนนี้ถึงแต่บ้านที่ อสม. ดูแล
 */
class VolaProcessV1 : VolaProcess {
    val logger = getLogger(this)
    override fun processUser(users: List<User>, persons: List<Person>): List<User> {
        val sha256 = SHA265()
        val surveyor = users.filter { it.roles.contains(User.Role.SURVEYOR) }
        return surveyor.mapNotNull { user ->
            val userIdCard = user.link?.keys?.get("idcard")?.toString() ?: return@mapNotNull null

            val findPerson = persons.find { person ->
                person.getIdCard()?.let {
                    sha256.hash(it) == userIdCard
                } ?: false
            }
            val personFindPid = findPerson?.link?.keys?.get("pid")?.toString()
            if (personFindPid != null && user.checkOkAdd(personFindPid)) {
                user.copy().apply {
                    bundle["pid"] = personFindPid
                }
            } else {
                logger.debug { "Cannot map pid user ${user.name}" }
                null
            }
        }
    }

    /**
     * ตรวจสอบดูว่ามีการกำหนดค่าเดิมซ้ำกันหรือไม่
     */
    private fun User.checkOkAdd(pid: String): Boolean {
        val thisPid = bundle["pid"]?.toString() ?: return true
        return thisPid != pid
    }

    override fun processHouse(houses: List<House>, users: List<User>): List<House> {
        val houseHaveAllowUser = houses.filter {
            val pcuvola = it.link?.keys?.get("pcucodepersonvola")?.toString()
            val pidvola = it.link?.keys?.get("pidvola")?.toString()
            pcuvola != null && pidvola != null
        }
        return houseHaveAllowUser.mapNotNull { house ->
            val pcuvola = house.link!!.keys["pcucodepersonvola"]!!.toString()
            val pidvola = house.link!!.keys["pidvola"]!!.toString()
            val user = users.find { user ->
                val pcucode = user.link!!.keys["pcucode"]?.toString()
                val pid = user.bundle["pid"]?.toString()
                if (pcucode != null && pid != null) {
                    pcucode == pcuvola && pid == pidvola
                } else
                    false
            }
            val userId = user?.id
            if (userId != null && house.checkOkAdd(userId)) {
                house.copy().apply {
                    allowUserId.add(userId)
                }
            } else
                null
        }
    }

    private fun Person.getIdCard(): String? = identities.firstOrNull()?.id

    /**
     * ตรวจสอบดูว่ามีการกำหนดค่าเดิมซ้ำกันหรือไม่
     */
    private fun House.checkOkAdd(userId: String): Boolean = !this.allowUserId.contains(userId)
}

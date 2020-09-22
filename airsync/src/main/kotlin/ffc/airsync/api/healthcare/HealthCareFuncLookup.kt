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

package ffc.airsync.api.healthcare

import ffc.airsync.api.person.getPcuCode
import ffc.airsync.api.person.getPid
import ffc.airsync.db.DatabaseDao.LookupHealthCareService
import ffc.airsync.homeHealthTypeApi
import ffc.airsync.icd10Api
import ffc.airsync.specialPpApi
import ffc.airsync.utils.getLogger
import ffc.entity.Person
import ffc.entity.User
import ffc.entity.healthcare.CommunityService
import ffc.entity.healthcare.Icd10
import ffc.entity.healthcare.SpecialPP
import java.util.SortedMap

class HealthCareFuncLookup(private val func: () -> Func) : LookupHealthCareService {

    interface Func {
        val users: List<User>
        val persons: List<Person>
        fun syncUser()
        fun syncPerson()
    }

    private var cacheSearchPerson = personSortedMap(func().persons)
    private var cacheSearchUser = userSortedMap(func().users)

    private fun personSortedMap(persons: List<Person>): SortedMap<String, Person> {
        return persons.map {
            "${it.getPcuCode()}:${it.getPid()}" to it
        }.toMap().toSortedMap()
    }

    private fun userSortedMap(user: List<User>): SortedMap<String, User> {
        return user.map {
            it.name.trim() to it
        }.toMap().toSortedMap()
    }

    override fun lookupPatientId(pcuCode: String, pid: String): String? {
        val search = cacheSearchPerson["$pcuCode:$pid"]?.id
        return if (search == null) {
            func().syncPerson()
            cacheSearchPerson = personSortedMap(func().persons)
            val search2 = cacheSearchPerson["$pcuCode:$pid"]?.id
            if (search2.isNullOrBlank()) logger.warn(
                NoSuchElementException("Lookup user")
            ) { "Lookup ไม่พบข้อมูลคน pcucode:$pcuCode pid:$pid ใน cloud" }
            search2
        } else
            search
    }

    override fun lookupProviderId(name: String): String? {
        val search = func().users.find { it.name.trim() == name.trim() }?.id
        return if (search == null) {
            func().syncUser()
            cacheSearchUser = userSortedMap(func().users)
            val search2 = func().users.find { it.name.trim() == name.trim() }?.id
            if (search2.isNullOrBlank()) logger.warn(NullPointerException()) {
                "Lookup user Error $name " +
                        "in ${
                            cacheSearchUser.map {
                                "${it.key}:${it.value.name}, "
                            }
                        }"
                // TODO ต้องพัฒนาเพิ่มถ้าค้นหาไม่เจอให้สร้างใหม่แต่สร้างในแบบที่ login ไม่ได้
            }
            search2
        } else
            search
    }

    override fun lookupDisease(icd10: String): Icd10? = icd10Api.lookup(icd10)
    override fun lookupSpecialPP(ppCode: String): SpecialPP.PPType? = specialPpApi.lookup(ppCode.trim())
    override fun lookupServiceType(serviceId: String): CommunityService.ServiceType? =
        homeHealthTypeApi.lookup(serviceId)

    private val logger = getLogger(this)
}

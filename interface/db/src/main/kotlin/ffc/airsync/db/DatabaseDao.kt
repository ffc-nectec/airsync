/*
 * Copyright (c) 2018 NECTEC
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

import ffc.entity.Person
import ffc.entity.User
import ffc.entity.Village
import ffc.entity.healthcare.Chronic
import ffc.entity.healthcare.CommunityServiceType
import ffc.entity.healthcare.Disease
import ffc.entity.healthcare.HomeVisit
import ffc.entity.place.Business
import ffc.entity.place.House
import ffc.entity.place.ReligiousPlace
import ffc.entity.place.School

interface DatabaseDao {
    fun getDetail(): HashMap<String, String>

    fun getUsers(): List<User>

    fun getPerson(): List<Person>

    fun findPerson(pcucode: String, pid: Long): Person

    fun getHouse(): List<House>

    fun getHouse(whereString: String): List<House>

    fun getChronic(): List<Chronic>

    fun upateHouse(house: House)

    fun getHomeVisit(
        user: List<User>,
        person: List<Person>,
        lookupDisease: (icd10: String) -> Disease,
        lookupHealthType: (id: String) -> CommunityServiceType
    ): List<HomeVisit>

    fun createHomeVisit(
        homeVisit: HomeVisit,
        pcucode: String,
        pcucodePerson: String,
        patient: Person,
        username: String
    )

    fun getVillage(): List<Village>

    fun getBusiness(): List<Business>

    fun getSchool(): List<School>

    fun getTemple(): List<ReligiousPlace>
}

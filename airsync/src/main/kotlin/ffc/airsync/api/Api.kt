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

package ffc.airsync.api

import ffc.airsync.db.DatabaseDao
import ffc.entity.Chronic
import ffc.entity.House
import ffc.entity.Organization
import ffc.entity.Person
import ffc.entity.User

interface Api {
    fun registerOrganization(organization: Organization, url: String): Organization

    fun putUser(userInfoList: List<User>, org: Organization)

    fun putHouse(houseList: List<House>, org: Organization)

    fun putPerson(personList: List<Person>, org: Organization)

    fun putChronic(chronicList: List<Chronic>, org: Organization)

    fun putFirebaseToken(firebaseToken: HashMap<String, String>, org: Organization)

    fun getHouseAndUpdate(org: Organization, _id: String, databaseDao: DatabaseDao)
}

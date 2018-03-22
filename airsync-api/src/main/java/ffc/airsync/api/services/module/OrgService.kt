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

package ffc.airsync.api.services.module

import ffc.model.*
import java.util.*

interface OrgService {
    fun register(organization :Organization, KnownIp :String) :Organization

    fun getData(uuid :UUID) :Message<QueryAction>


    fun getMyOrg(ipAddress :String)   : List <Organization>

    fun createUser(token: String,
                   orgId :String,
                   userList : ArrayList<User>)


    fun orgUserAuth(id :String,user:String,pass:String) :TokenMessage



    fun sendEventGetData(uuid :UUID)
}

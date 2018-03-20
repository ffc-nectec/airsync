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

package ffc.airsync.client.client

import ffc.model.*

class CentralMessageMaorgUpdatenageV1 : CentralMessageManage {


    var organization: Organization?=null
    var urlBase: String?=null
    var stage:Int = 0
    override fun putUser(userInfoList: ArrayList<UserInfo>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerOrganization(organization :Organization, url :String) :Organization {
        this.organization=organization
        this.urlBase=url
        //val organization2:Organization  = putToServer(url,orgUuid.toJson()).body()!!.string().fromJson()
        val organization2:Organization  = organization.toJson().httpPost(url).body()!!.string().fromJson()

        return organization2
    }

    override fun checkMobileRegisterAuth(userAuthFilter: (userInfo : UserInfo) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.


    }

    override fun getData(): QueryAction {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}

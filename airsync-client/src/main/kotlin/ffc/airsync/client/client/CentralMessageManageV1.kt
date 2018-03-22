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

import ffc.airsync.client.client.module.ApiFactory
import ffc.model.Organization
import ffc.model.User
import javax.ws.rs.NotFoundException

class CentralMessageMaorgUpdatenageV1 : CentralMessageManage {


    var organization: Organization? = null
    var urlBase: String? = null
    var stage: Int = 0
    override fun putUser(userInfoList: ArrayList<User>, org: Organization) {

        val restService = ApiFactory().buildApiClient(Config.baseUrlRest)
        val org = restService!!.regisUser(user = userInfoList,orgId = org.id,authkey = "Bearer "+org.orgToken!!).execute().body()




    }




    override fun registerOrganization(organization: Organization, url: String): Organization {
        this.organization = organization
        this.urlBase = url

        //val organization2: Organization = organization.toJson().httpPost(url).body()!!.string().fromJson()
        val restService = ApiFactory().buildApiClient(Config.baseUrlRest)
        val org = restService!!.regisOrg(organization).execute().body()

        println(org)
        Thread.sleep(3000)

        if(org!=null)
            return org
        throw NotFoundException()
    }


}



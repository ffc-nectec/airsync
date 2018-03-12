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

class CentralMessageManageV1 : CentralMessageManage {


    var pcu: Pcu?=null
    var urlBase: String?=null
    var stage:Int = 0


    override fun registerPcu(pcu :Pcu, url :String) :Pcu {
        this.pcu=pcu
        this.urlBase=url
        //val pcu2:Pcu  = putToServer(url,pcu.toJson()).body()!!.string().fromJson()
        val pcu2:Pcu  = pcu.toJson().httpPut(url).body()!!.string().fromJson()

        return pcu2
    }

    override fun checkMobileRegisterAuth(userAuthFilter: (mobileUserAuth : MobileUserAuth) -> Unit) {
        val messageGetUserList = Message(from = pcu!!.uuid,action = Message.Action.GETUSER,status = Message.Status.DEFAULT,message = pcu!!.toJson())
        val userList :List<MobileUserAuth> =messageGetUserList.toJson().httpPost(urlBase!!).body()!!.string().fromJson() //get User list
        userList.forEach {
            println("User Get1 = "+ it.mobileUuid)
            userAuthFilter(it)
        }

    }

    override fun getData(): QueryAction {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}

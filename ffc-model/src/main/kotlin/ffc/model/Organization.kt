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

package ffc.model

import java.util.*

data class Organization(val uuid: UUID = UUID.randomUUID(),var id: String="-1") {



    constructor(uuid: UUID,id : String, pcuCode: String, name: String, pcuToken: String? = null, session: String?= null, lastKnownIp: String?= null,  deviceToken:String?=null, socketUrl:String? =null) : this(uuid,id) {

        this.pcuCode = pcuCode
        this.name = name
        this.token=pcuToken
        this.session=session
        this.lastKnownIp=lastKnownIp
        this.deviceToken=deviceToken
        this.socketUrl=socketUrl

    }

    var pcuCode: String = "099912"
    var name: String = "NECTEC"
    var session: String? = null
    var lastKnownIp: String? = null
    var token: String? = null
    var deviceToken: String? = null
    var socketUrl:String? = null


    override fun toString(): String {
        return "Organization(uuid=$uuid)"
    }

}

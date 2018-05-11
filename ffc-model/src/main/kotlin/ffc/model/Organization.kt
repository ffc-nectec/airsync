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

data class Organization(val uuid: UUID = UUID.randomUUID(), var id: String = "-1") {


    constructor(uuid: UUID, id: String, pcuCode: String, name: String, pcuToken: String? = null, session: String? = null, lastKnownIp: String? = null, socketUrl: String? = null) : this(uuid, id) {

        this.pcuCode = pcuCode
        this.name = name
        this.token = pcuToken
        this.session = session
        this.lastKnownIp = lastKnownIp
        this.socketUrl = socketUrl

    }

    var pcuCode: String = "099912"
    var name: String = "NECTEC"
    var session: String? = null
    var lastKnownIp: String? = null
    var token: String? = null
    var socketUrl: String? = null
    var firebaseToken: String? = null

    fun clone(): Organization {
        val orgClone = Organization(
          uuid = UUID.fromString(this.uuid.toString()),
          id = this.id)

        orgClone.pcuCode = this.pcuCode
        orgClone.name = this.name
        orgClone.session = this.session
        orgClone.lastKnownIp = this.lastKnownIp
        orgClone.token = this.token
        orgClone.socketUrl = this.socketUrl
        orgClone.firebaseToken = this.firebaseToken


        return orgClone
    }


    override fun toString(): String {
        return "Organization(uuid=$uuid)"
    }

}

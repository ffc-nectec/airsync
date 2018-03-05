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

data class Pcu(val uuid: UUID = UUID.randomUUID()) {
    var code: String = "099912"
    var name: String = "NECTEC"


    constructor(uuid: UUID, code: String, name: String, pcuToken: String? = null, session: String?= null, lastKnownIp: String?= null, centralToken: String?= null) : this(uuid) {
        this.code = code
        this.name = name
        this.pcuToken=pcuToken
        this.session=session
        this.lastKnownIp=lastKnownIp
        this.centralToken=centralToken

    }



    var session: String? = null
    var lastKnownIp: String? = null
    var pcuToken: String? = null
    var centralToken: String? =null

    override fun toString(): String {
        return "Pcu(uuid=$uuid)"
    }

}

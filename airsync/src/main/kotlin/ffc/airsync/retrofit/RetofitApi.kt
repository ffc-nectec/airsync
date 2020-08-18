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

package ffc.airsync.retrofit

import ffc.airsync.Config
import ffc.entity.Organization
import ffc.entity.Token

abstract class RetofitApi<T>(
    retofitUrl: Class<T>,
    cacheKbyte: Int = 2048
) {

    companion object {
        lateinit var organization: Organization
        lateinit var token: Token
    }

    val urlBase: String
        get() = Config.baseUrlRest
    val tokenBarer: String
        get() = "Bearer " + token.token
    val pcucode: String
        get() = (organization.link!!.keys["pcucode"] as String).trim()
    val restService = ApiFactory().buildApiClient(urlBase, retofitUrl, cacheKbyte)
}

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

package ffc.airsync.api.services.filter


import org.glassfish.jersey.internal.util.Base64
import java.util.*
import javax.ws.rs.container.ContainerRequestContext


object BasicAuthInfo {

    private val AUTHORIZATION_PROPERTY = "Authorization"
    private val AUTHENTICATION_SCHEME = "Basic"

    private var username: String?=null
    private var password: String?=null

    fun BasicAuthInfo(requestContext: ContainerRequestContext) {
        val authorization = requestContext.headers[AUTHORIZATION_PROPERTY]
        val encodedUserPassword = authorization?.get(0)?.replaceFirst("$AUTHENTICATION_SCHEME ".toRegex(), "")

        val userPassword = String(Base64.decode(encodedUserPassword?.toByteArray()))
        val tokenizer = StringTokenizer(userPassword, ":")
        username = tokenizer.nextToken()
        password = tokenizer.nextToken()
    }

    fun hasAuthorizeProperty(requestContext: ContainerRequestContext): Boolean {
        val authorization = requestContext.headers[AUTHORIZATION_PROPERTY]
        return !(authorization == null || authorization.isEmpty())
    }

    fun getUsername(): String {

        return this!!.username!!
    }

    fun getPassword(): String {
        return password!!
    }


}

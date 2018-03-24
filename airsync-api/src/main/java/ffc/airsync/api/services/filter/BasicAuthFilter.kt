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

import ffc.model.Organization
import ffc.model.UserInfo
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter


class BasicAuthFilter :ContainerRequestFilter{

    //private val UNAUTHORIZED = ( "ชื่อ หรือ รหัสผ่านไม่ถูกต้อง")
    //private val FORBIDDEN = ErrorMessage(403, "คุณไม่มีสิทธิเข้าใข้งาน")


    override fun filter(requestContext: ContainerRequestContext) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
/*
        if (!BasicAuthInfo.hasAuthorizeProperty(requestContext))
            return


        val authenInfo = BasicAuthInfo.BasicAuthInfo(requestContext)

        */

        //val userDao = DaoFactory.build(UserDao::class.java)
        /*val username = userDao.findCall(authenInfo.getUsername(), authenInfo.getPassword())
          ?: throw FaarmisException(401, UNAUTHORIZED)
        if (!isAllow(username))
            throw FaarmisException(403, FORBIDDEN)
*/


        /*
        val urlScheme = requestContext.uriInfo.baseUri.scheme //http or https
        val securityContext = SecurityContextImp(username, urlScheme)
        requestContext.securityContext = securityContext
        */
    }





    private fun isAllow(userInfo: UserInfo): Organization {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
/*

        if(userInfo.username=="adminffcair" && userInfo.password=="ffc@irffc@ir")
        {
            return Organization(uuid = UUID.fromString(userInfo.orgUuid.uuid.toString()),name = "รพสต. เนคเทค",pcuCode = "283",deviceToken = "00xad")
        }
        else if(userInfo.username=="adminffcair520" && userInfo.password=="ffc@irffc@ir520")
        {
            return Organization(uuid = UUID.fromString(userInfo.orgUuid.uuid.toString()),name = "รพสต. ห้าสองศูนย์บี",pcuCode = "288",deviceToken = "00xdfvad")
        }

        throw NotFoundException()
        */

    }

}


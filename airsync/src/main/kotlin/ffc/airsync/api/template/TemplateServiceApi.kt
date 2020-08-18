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

package ffc.airsync.api.template

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi
import ffc.entity.Template
import javax.ws.rs.NotAuthorizedException

class TemplateServiceApi : RetofitApi<TemplateService>(TemplateService::class.java), TemplateApi {
    override fun clearAndCreate(template: List<Template>) {
        callApi {
            val response = restService.clearnAndCreate(
                orgId = organization.id,
                authkey = tokenBarer,
                template = template
            ).execute()

            if (response.code() != 201)
                if (response.code() == 401) {
                    var errorBody = ""
                    response.errorBody()?.byteStream()?.reader()?.readLines()?.let { error ->
                        error.forEach {
                            errorBody += it + "\r\n"
                        }
                    }
                    throw NotAuthorizedException("Create Template code ${response.code()} $errorBody")
                }
        }
    }
}

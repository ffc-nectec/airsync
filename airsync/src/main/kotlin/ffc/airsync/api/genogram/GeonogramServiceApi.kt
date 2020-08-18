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

package ffc.airsync.api.genogram

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.UploadSpliterMap
import ffc.airsync.utils.callApi
import ffc.airsync.utils.callApiNoReturn
import ffc.airsync.utils.getLogger
import ffc.entity.Person

class GeonogramServiceApi : RetofitApi<GenogramService>(GenogramService::class.java), GeonogramApi {
    private val logger by lazy { getLogger(this) }
    override fun put(personId: String, relationship: List<Person.Relationship>): List<Person.Relationship> {
        val relationLastUpdate = arrayListOf<Person.Relationship>()
        var syncccc = false
        var loop = 1
        while (!syncccc) {
            try {
                logger.debug("Sync rela loop ${loop++} ")
                val response = restService.updateRelationship(
                    orgId = organization.id,
                    authkey = tokenBarer,
                    personId = personId,
                    relationship = relationship
                ).execute()
                logger.debug(" response ${response.code()} err:${response.errorBody()?.source()}")
                if (response.code() == 201 || response.code() == 200) {
                    relationLastUpdate.addAll(response.body() ?: arrayListOf())
                    syncccc = true
                } else {
                    syncccc = false
                }
            } catch (ex: Exception) {
                syncccc = false
                Thread.sleep(5000)
            }
        }
        return relationLastUpdate
    }

    override fun putBlock(
        relationship: Map<String, List<Person.Relationship>>,
        progressCallback: (Int) -> Unit
    ): Map<String, List<Person.Relationship>> {
        val output = hashMapOf<String, List<Person.Relationship>>()
        callApiNoReturn { restService.cleanAll(organization.id, tokenBarer).execute() }
        val fixSizeCake = 100
        val sizeOfLoop = relationship.size / fixSizeCake
        UploadSpliterMap.upload(fixSizeCake, relationship) { list, block ->

            val result = callApi {
                restService.unConfirmBlock(organization.id, tokenBarer, block).execute()

                val response = restService.insertBlock(
                    organization.id, tokenBarer,
                    block = block,
                    relationship = list
                ).execute()

                if (response.code() == 201 || response.code() == 200) {
                    restService.confirmBlock(
                        organization.id, tokenBarer,
                        block = block
                    )
                    response.body()
                } else {
                    throw ApiLoopException("Response code wrong.")
                }
            }
            output.putAll(result)
            if (sizeOfLoop != 0)
                progressCallback(((block * 45) / sizeOfLoop) + 50)
        }
        return output
    }
}

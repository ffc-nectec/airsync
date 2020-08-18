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

package ffc.airsync.api.person

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.UploadSpliter
import ffc.airsync.utils.callApi
import ffc.airsync.utils.callApiNoReturn
import ffc.entity.Person
import retrofit2.dsl.enqueue

class PersonServiceApi : RetofitApi<PersonService>(PersonService::class.java), PersonApi {
    override fun putPerson(
        personList: List<Person>,
        progressCallback: (Int) -> Unit,
        clearCloud: Boolean
    ): List<Person> {
        val personLastUpdate = arrayListOf<Person>()
        if (clearCloud)
            callApiNoReturn { restService.clearnPerson(orgId = organization.id, authkey = tokenBarer).execute() }

        val fixSizeCake = 200
        val sizeOfLoop = personList.size / fixSizeCake
        UploadSpliter.upload(fixSizeCake, personList) { it, index ->

            val result = callApi {
                restService.unConfirmPersonBlock(
                    orgId = organization.id,
                    authkey = tokenBarer,
                    block = index
                ).execute()

                val response = restService.insertPersonBlock(
                    orgId = organization.id,
                    authkey = tokenBarer,
                    personList = it,
                    block = index
                ).execute()

                if (response.code() == 201 || response.code() == 200) {
                    restService.confirmPersonBlock(
                        orgId = organization.id,
                        authkey = tokenBarer,
                        block = index
                    ).enqueue { }
                    response.body() ?: arrayListOf()
                } else {
                    throw ApiLoopException("Cannot Login ${response.code()}")
                }
            }
            personLastUpdate.addAll(result)
            if (sizeOfLoop != 0)
                progressCallback(((index * 50) / sizeOfLoop) + 50)
        }
        return personLastUpdate
    }
}

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

package ffc.airsync.api.village

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.callApi
import ffc.entity.Village

class VillageServiceApi : RetofitApi<VillageService>(VillageService::class.java), VillageApi {
    override fun toCloud(villages: List<Village>, clearCloud: Boolean): List<Village> {
        return callApi {
            val output = arrayListOf<Village>()
            callApi { restService.deleteVillage(organization.id, tokenBarer).execute() }
            /*val response = restService.create(
                orgId = organization.id,
                authkey = tokenBarer,
                villages = villages
            ).execute()*/

            villages.forEach {
                it.places.clear()

                val response = restService.createSingel(
                    orgId = organization.id,
                    authkey = tokenBarer,
                    village = it
                ).execute()

                if (response.code() == 201 || response.code() == 200) {
                    output.add(response.body()!!)
                } else
                    throw ApiLoopException("create village error code ${response.code()}")
            }

            output
        }
    }

    override fun get(): List<Village> {
        return callApi {
            val cloud = restService.getHouse(organization.id, tokenBarer).execute()
            cloud.body() ?: arrayListOf()
        }
    }

    override fun editCloud(village: Village): Village {
        return callApi {
            val response = restService.edit(
                organization.id, tokenBarer,
                villageId = village.id,
                villages = village
            ).execute()
            if (response.code() == 201 || response.code() == 200) {
                response.body()
            } else
                throw ApiLoopException("Edit village error code ${response.code()}")
        }
    }
}

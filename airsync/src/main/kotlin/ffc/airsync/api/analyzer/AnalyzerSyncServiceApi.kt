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

package ffc.airsync.api.analyzer

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.UploadSpliterMap
import ffc.airsync.utils.callApi
import ffc.airsync.utils.callApiNoReturn
import ffc.entity.healthcare.analyze.HealthAnalyzer

class AnalyzerSyncServiceApi : RetofitApi<AnalyzerService>(AnalyzerService::class.java), AnalyzerSyncApi {

    override fun insert(
        healtyAnalyzer: Map<String, HealthAnalyzer>,
        progressCallback: (Int) -> Unit
    ): Map<String, HealthAnalyzer> {
        val output = hashMapOf<String, HealthAnalyzer>()
        callApiNoReturn { restService.cleanHealthAnalyzeOrgId(organization.id, tokenBarer).execute() }

        val fixSizeCake = 200
        val sizeOfLoop = healtyAnalyzer.size / fixSizeCake
        UploadSpliterMap.upload(fixSizeCake, healtyAnalyzer) { analyzer, block ->

            val result = callApi {
                restService.unConfirmBlock(organization.id, tokenBarer, block).execute()
                val response = restService.insertBlock(
                    organization.id, tokenBarer,
                    block = block,
                    healtyAnalyzer = analyzer
                ).execute()
                if (response.code() == 201 || response.code() == 200) {
                    response.body()
                } else {
                    throw ApiLoopException("Response code wrong.")
                }
            }

            output.putAll(result)
            if (sizeOfLoop != 0)
                progressCallback(((block * 50) / sizeOfLoop) + 50)
        }
        return output
    }
}

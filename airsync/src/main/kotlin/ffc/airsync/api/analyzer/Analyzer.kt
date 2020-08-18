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

import ffc.airsync.analyzerSyncApi
import ffc.airsync.api.analyzer.lib.NewProcessAnalyzer
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.analyze.HealthAnalyzer

fun HashMap<String, HealthAnalyzer>.initSync(
    healthCareService: List<HealthCareService>,
    progressCallback: (Int) -> Unit
) {

    val localAnalyzer = hashMapOf<String, HealthAnalyzer>()
    localAnalyzer.putAll(localAnalyzer.load("analyzer.json"))

    if (localAnalyzer.isEmpty()) {
        val processCloud = hashMapOf<String, HealthAnalyzer>()
        processCloud.putAll(
            analyzerSyncApi.insert(
                NewProcessAnalyzer().analyzerGroup(healthCareService),
                progressCallback
            )
        )

        localAnalyzer.clear()
        localAnalyzer.putAll(processCloud)

        localAnalyzer.save("analyzer.json")
    } else {
        putAll(localAnalyzer)
    }
    progressCallback(100)
}

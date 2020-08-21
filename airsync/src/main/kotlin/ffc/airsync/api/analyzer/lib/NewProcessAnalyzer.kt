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

package ffc.airsync.api.analyzer.lib

import ffc.airsync.api.analyzer.lib.NewAnalyzer.Tag.OK
import ffc.airsync.api.analyzer.lib.NewAnalyzer.Tag.StickBed
import ffc.airsync.api.analyzer.lib.NewAnalyzer.Tag.StickHouse
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.Severity.LOW
import ffc.entity.healthcare.Severity.MID
import ffc.entity.healthcare.analyze.HealthAnalyzer
import ffc.entity.healthcare.analyze.HealthIssue.Issue.ACTIVITIES
import ffc.entity.healthcare.analyze.HealthProblem

class NewProcessAnalyzer : NewAnalyzer {
    override fun analyzer(healthCareServices: List<HealthCareService>): HealthAnalyzer {
        val analyzer = HealthAnalyzer()
        analyzer.analyze(*healthCareServices.toTypedArray())
        return analyzer
    }

    override fun analyzerGroup(
        healthCareServices: List<HealthCareService>
    ): Map<String, HealthAnalyzer> {
        // สร้าง List รายการคนสำหรับประมวลผล
        val processList = createProcessList(healthCareServices)

        return processList.map { item ->
            item.key to analyzer(item.value)
        }.toMap()
    }

    private fun createProcessList(
        healthCareServices: List<HealthCareService>
    ): HashMap<String, ArrayList<HealthCareService>> {
        val processList = hashMapOf<String, ArrayList<HealthCareService>>()
        healthCareServices.forEach {
            if (processList[it.patientId] == null) processList[it.patientId] = arrayListOf()
            processList[it.patientId]!!.add(it)
        }
        return processList
    }

    override fun analyzerGroupAutoAddTag(
        healthCareServices: List<HealthCareService>,
        addTag: () -> NewAnalyzer.AddTag
    ): Map<String, HealthAnalyzer> {
        // สร้าง List รายการคนสำหรับประมวลผล
        val processList = createProcessList(healthCareServices)

        return processList.map { item ->
            val analyzer = analyzer(item.value)

            // หาผู้สูงอายุติดบ้าน ติดเตียง
            val patientId = item.key
            if (addTag().getAge(patientId) >= 60 && addTag().isLife(patientId)) {
                analyzer.result[ACTIVITIES]?.let {
                    when ((it as HealthProblem).severity) {
                        MID -> addTag().addTag(patientId, StickHouse)
                        LOW -> addTag().addTag(patientId, StickBed)
                        else -> addTag().addTag(patientId, OK)
                    }
                }
            } else {
                addTag().removeTag(patientId, StickHouse)
                addTag().removeTag(patientId, StickBed)
                addTag().removeTag(patientId, OK)
            }
            patientId to analyzer
        }.toMap()
    }
}

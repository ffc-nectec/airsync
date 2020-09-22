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
import ffc.airsync.api.analyzer.lib.NewAnalyzer
import ffc.airsync.api.analyzer.lib.NewAnalyzer.Tag.OK
import ffc.airsync.api.analyzer.lib.NewAnalyzer.Tag.StickBed
import ffc.airsync.api.analyzer.lib.NewAnalyzer.Tag.StickHouse
import ffc.airsync.api.analyzer.lib.NewProcessAnalyzer
import ffc.airsync.houseManage
import ffc.airsync.personManage
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.analyze.HealthAnalyzer
import ffc.entity.place.House

class SyncAnalyzer(private val healthCareService: List<HealthCareService>) {
    fun sync() {
        val processCloud = hashMapOf<String, HealthAnalyzer>()
        val listHouseUpdate = arrayListOf<House?>()

        processCloud.putAll(
            analyzerSyncApi.insert(
                NewProcessAnalyzer().analyzerGroupAutoAddTag(healthCareService) {
                    object : NewAnalyzer.AddTag {
                        private val cachePersonLookup = personManage.cloud.map { it.id to it }.toMap().toSortedMap()
                        private val cacheHouseLookup = houseManage.cloud.map { it.id to it }.toMap().toSortedMap()

                        override fun addTag(patientId: String, tag: NewAnalyzer.Tag) {
                            val findHouseId = cachePersonLookup[patientId]?.houseId ?: return
                            val house = cacheHouseLookup[findHouseId] ?: return

                            when (tag) {
                                StickBed -> listHouseUpdate.add(house.addTag("elder-activities-mid"))
                                StickHouse -> listHouseUpdate.add(house.addTag("elder-activities-very_hi"))
                                OK -> listHouseUpdate.add(house.addTag("elder-activities-ok"))
                            }
                        }

                        override fun removeTag(patientId: String, tag: NewAnalyzer.Tag) {
                            val findHouseId = cachePersonLookup[patientId]?.houseId ?: return
                            val house = cacheHouseLookup[findHouseId] ?: return

                            when (tag) {
                                StickBed -> listHouseUpdate.add(house.removeTag("elder-activities-mid"))
                                StickHouse -> listHouseUpdate.add(house.removeTag("elder-activities-very_hi"))
                                OK -> listHouseUpdate.add(house.removeTag("elder-activities-ok"))
                            }
                        }

                        override fun getAge(patientId: String): Int = cachePersonLookup[patientId]?.age ?: 0
                        override fun isLife(patientId: String): Boolean =
                            !(cachePersonLookup[patientId]?.isDead ?: true)
                    }
                }
            ) {}
        )
        if (listHouseUpdate.isNotEmpty()) {
            houseManage.directUpdateCloudData(listHouseUpdate.mapNotNull { it })
        }
    }

    private fun House.addTag(tagName: String): House? {
        if (tags.contains(tagName)) return null
        tags.add(tagName)
        return this
    }

    private fun House.removeTag(tagName: String): House? {
        return if (tags.contains(tagName)) {
            tags.remove(tagName)
            this
        } else null
    }
}

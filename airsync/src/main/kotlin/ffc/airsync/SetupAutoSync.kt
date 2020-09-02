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

package ffc.airsync

import ffc.airsync.api.person.SyncPerson
import ffc.airsync.api.person.initSync
import ffc.airsync.api.pidvola.VolaProcess
import ffc.airsync.api.pidvola.VolaProcessV1
import ffc.airsync.api.tag.Level1TagProcess
import ffc.airsync.api.template.TemplateInit
import ffc.airsync.api.village.initSync
import ffc.airsync.db.DatabaseDao
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.syncCloud
import ffc.entity.Person
import ffc.entity.place.House
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class SetupAutoSync(val dao: DatabaseDao) {

    init {
        setUpAutoSync()
    }

    private val logger by lazy { getLogger(this) }

    private fun setUpAutoSync() {
        autoSyncFromCloud().start()
        autoSyncToCloud().start()
    }

    private fun autoSyncFromCloud(): Thread {
        return Thread {
            while (true) {
                try {
                    syncCloud.sync(dao)
                    syncVola()
                    syncTags()
                } catch (ignore: Exception) {
                    ignore.printStackTrace()
                    logger.error("Auto sync error(will auto rerun). Error:${ignore.message}", ignore)
                }

                Thread.sleep(60000)
            }
        }
    }

    private fun syncTags() {
        val updateHouse = arrayListOf<House>()
        Level1TagProcess(persons, houseManage.cloud) {
            object : Level1TagProcess.UpdateData {
                override fun updateHouse(house: House) {
                    try {
                        updateHouse.add(house)
                    } catch (ex: Exception) {
                        logger.warn(ex) { "Tag update house error" }
                    }
                }

                override fun updatePerson(person: Person) {
                    // TODO ตอนนี้ใช้ดูแค่บ้าน
                }
            }
        }.process()

        houseManage.directUpdateCloudData(updateHouse)
    }

    private fun syncVola() {
        val volaProcess: VolaProcess = VolaProcessV1()
        val volaUser = volaProcess.processUser(userManage.cloudUser, persons)
        val volaHouse = volaProcess.processHouse(houseManage.cloud, volaUser)
        houseManage.directUpdateCloudData(volaHouse)
    }

    private fun autoSyncToCloud(): Thread {
        return Thread {
            while (true) {
                try {
                    delaySync()
                    logger.info("Sync template")
                    runCatching { TemplateInit() }
                    logger.info("Sync user")
                    runCatching { userManage.sync() }
                    logger.info("Sync village")
                    runCatching { villages.initSync() }
                    runCatching {
                        logger.info("Sync person")
                        val syncPerson = SyncPerson()
                        val jhcisDbPerson = syncPerson.prePersonProcess()
                        houseManage.sync()
                        logger.info("Sync house")
                        persons.initSync(houseManage.cloud, jhcisDbPerson) {}
                    }
                } catch (ignore: Exception) {
                    ignore.printStackTrace()
                } finally {
                    countSync = -100
                }
            }
        }
    }

    private fun delaySync() {
        runBlocking {
            val min: Long = 60000
            while (countSync == -100) { // -100 is stop
                delay(min)
            }
            while (countSync > 0) {
                countSync--
                delay(min)
            }
        }
    }
}

fun turnOnSync(min: Int = 60) {
    countSync = min
}

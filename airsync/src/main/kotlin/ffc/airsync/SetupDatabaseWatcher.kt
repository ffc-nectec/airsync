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

import ffc.airsync.api.healthcare.lock
import ffc.airsync.api.person.SyncPerson
import ffc.airsync.api.person.initSync
import ffc.airsync.api.village.VILLAGELOOKUP
import ffc.airsync.api.village.initSync
import ffc.airsync.db.DatabaseDao
import ffc.airsync.utils.callApi
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.jobFFC
import ffc.airsync.utils.save
import ffc.entity.copy
import ffc.entity.healthcare.HealthCareService
import ffc.entity.place.House
import ffc.entity.update

class SetupDatabaseWatcher(val dao: DatabaseDao) {

    init {
        databaseWatcher()
    }

    private val logger by lazy { getLogger(this) }

    private fun databaseWatcher() {
        /**
         * รูปแบบคือ ชื่อ , list รูปแบบ table string ที่จะให้ตรวจจับ
         * แต่ยังไม่ได้แยกว่า เป็น select, update, delete
         */
        val tablesPattern = hashMapOf<String, List<String>>().apply {
            put("house", listOf("house", "`house`", "`jhcisdb`.`house`"))
            put("visit", listOf("visit", "`visit`", " visit ", "visitdrug", "visithomehealthindividual"))
            put("person", listOf("`person`", " person ", "`jhcisdb`.`person`", "person"))
            put("user", listOf("`user`", " user ", "`jhcisdb`.`user`"))
        }

        ffc.airsync.provider.databaseWatcher(
            Config.logfilepath, tablesPattern, { isShutdown }
        ) { tableName, keyWhere ->
            logger.info("Database watcher $tableName $keyWhere")
            when (tableName) {
                "house" -> houseEvent(keyWhere)
                "visit" -> visitEvent(keyWhere)
                "person" -> personEvent(keyWhere)
                "user" -> userEvent(keyWhere)
            }
        }.start()
    }

    private fun personEvent(keyWhere: List<String>) {
        val pattern = Regex("""^.*pcucodeperson[` ]?='?(\d+)'?.*pid[` ]?='?(\d+)'?.*$""")
        if (pattern.matches(keyWhere.firstOrNull() ?: "")) {
            turnOnSync()
        }
    }

    private fun userEvent(keyWhere: List<String>) {
        if (keyWhere.size == 1) {
            val pattern = Regex("""^.*pcucode[` ]?='?(\d+)'?.*username[` ]?='?(.+)'?.*$""")
            if (pattern.matches(keyWhere[0]))
                turnOnSync()
        }
    }

    private fun houseEvent(keyWhere: List<String>) {
        jobFFC {
            val pattern = Regex("""^.*pcucode[` ]?='?(\d+)'?.*hcode[` ]?='?(\d+)'? AND \d+$""")
            if (pattern.matches(keyWhere.firstOrNull() ?: ""))
                turnOnSync()
            if (keyWhere.size == 1) {
                val house = runCatching { dao.getHouse(VILLAGELOOKUP, keyWhere.first()) }.getOrDefault(listOf())
                house.forEach {
                    try {
                        val houseSync = findHouseWithKey(it)
                        houseSync.update(it.timestamp) {
                            road = it.road
                            no = it.no
                            location = it.location
                            link!!.isSynced = true
                        }

                        houseApi.update(houseSync)
                    } catch (ignore: NullPointerException) {
                    }
                }
            }
        }
    }

    /**
     * เมื่อเกิดเหตุการที่ตาราง visit ให้ทำ
     * @param keyWhere ค่า where จาก sql
     */
    private fun visitEvent(keyWhere: List<String>) {
        jobFFC {
            when (keyWhere.size) {
                1 -> { // พฤติกรรมของ update where
                    val sqlWhere = keyWhere.first()
                    val pattern = listOf(
                        Regex("""^.*pcucode[` ]?='?(\d+)'?.*visitno[` ]?='?(\d+)'?.*$""")
                    )
                    val visitMatchValue: (List<Regex>, String) -> List<String> =
                        { reg, where ->
                            var output = listOf<String>()
                            reg.forEach {
                                val match = it.matchEntire(where)?.groupValues
                                if (match?.size == 3 && output.isEmpty()) {
                                    output = match
                                }
                            }
                            output
                        }
                    val aggregate = visitMatchValue(pattern, sqlWhere)
                    if (aggregate.size == 3) {
                        val pcucode = aggregate[1]
                        val visitno = aggregate[2].toLongOrNull()
                        visitToCloud(pcucode, visitno, sqlWhere)
                    }
                }
                2 -> { // พฤติกรรม Insert
                    val map = keyWhereIsInsertQueryToMapKeyValue(keyWhere)
                    val pcucode = map["pcucode"]
                    val visitno = map["visitno"]?.toLongOrNull()

                    if (pcucode != null) {
                        val sqlWhere = "pcucode ='$pcucode' AND visitno ='$visitno'"
                        visitToCloud(pcucode, visitno, sqlWhere)
                    }
                }
                else -> {
                    logger.warn("Insert where size ${keyWhere.size}")
                }
            }
            logger.debug("visit k:$keyWhere")
        }
    }

    private fun keyWhereIsInsertQueryToMapKeyValue(keyWhere: List<String>): HashMap<String, String> {
        val sqlSetKey = keyWhere.first().replace(Regex("""['`]"""), "").split(',')
        val sqlValueKey = keyWhere.last().replace(Regex("""['`]"""), "").split(',')
        val map = hashMapOf<String, String>()
        if (sqlSetKey.size == sqlValueKey.size) {
            sqlSetKey.forEachIndexed { index, key ->
                map[key] = sqlValueKey[index]
            }
        }
        return map
    }

    private fun visitToCloud(pcucode: String, visitno: Long?, updateWhere: String) {
        logger.info("Create new visit to cloud")
        jobFFC {
            visitno?.let { visitNo ->
                val visitJhcis = getHealthCareFromDb(updateWhere)

                val cloudFind = healthCare.find {
                    val checkPcu = it.link?.keys?.get("pcucode").toString() == pcucode
                    val checkVisit =
                        it.link?.keys?.get("visitno").toString() == visitNo.toString()
                    checkPcu && checkVisit
                }

                if (cloudFind == null) {
                    val fromCloud = callApi { healthCareApi.createHealthCare(visitJhcis) {} }
                    healthCare.lock {
                        healthCare.addAll(fromCloud)
                        healthCare.save()
                    }
                } else {
                    visitJhcis.forEach { it ->
                        it.link = cloudFind.link
                        it.link?.isSynced = true
                        val update = healthCareApi.updateHealthCare(it.copy(cloudFind.id))
                        healthCare.lock {
                            healthCare.removeIf { it.id == update.id }
                            healthCare.add(update)
                            healthCare.save()
                        }
                    }
                }
            }
        }
    }

    private fun getHealthCareFromDb(updateWhere: String): List<HealthCareService> {
        return dao.getHealthCareService(
            lookupPatientId = lookupPersonId,
            lookupProviderId = lookupUserId,
            lookupDisease = lookupDisease,
            lookupServiceType = lookupServiceType,
            lookupSpecialPP = lookupSpecialPP,
            whereString = updateWhere
        )
    }

    private fun findHouseWithKey(house: House): House {
        val houseFind = houseManage.cloud.find {
            house.link!!.keys["pcucode"] == it.link!!.keys["pcucode"] &&
                    house.link!!.keys["hcode"] == it.link!!.keys["hcode"]
        }

        if (houseFind == null) {
            val syncPerson = SyncPerson()
            val jhcisDbPerson = syncPerson.prePersonProcess()
            villages.initSync()
            houseManage.sync()
            persons.initSync(houseManage.cloud, jhcisDbPerson) {}
            return houseManage.cloud.find {
                house.link!!.keys["pcucode"] == it.link!!.keys["pcucode"] &&
                        house.link!!.keys["hcode"] == it.link!!.keys["hcode"]
            } ?: throw NullPointerException("ค้นหาไม่พบบ้าน")
        } else
            return houseFind
    }
}

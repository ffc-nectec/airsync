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

import ffc.airsync.Main
import ffc.airsync.api.sync.ProSync
import ffc.airsync.api.sync.V1ProSync
import ffc.airsync.db.DatabaseDao
import ffc.airsync.houseManage
import ffc.airsync.utils.`อัพเดทไปยัง`
import ffc.airsync.utils.ffcFileLoad
import ffc.airsync.utils.ffcFileSave
import ffc.airsync.utils.getLogger
import ffc.entity.Entity
import ffc.entity.Person
import ffc.entity.copy
import ffc.entity.healthcare.Icd10
import java.io.File
import java.util.SortedMap

class PersonManage(
    val dao: DatabaseDao = Main.instant.dao,
    private val func: () -> Func
) : PersonInterface {

    private val personApi: PersonApi = PersonServiceApi()
    private var cloudCache = arrayListOf<Person>()
    private val file = File("data", "person.json")
    private val logger = getLogger(this)

    init {
        cloudCache.addAll(ffcFileLoad(file))
    }

    interface Func {
        fun lookupDisease(icd10: String): Icd10
    }

    override val local: List<Person>
        get() {
            val houseId = cacheHouseId()
            val persons = dao.getPerson { func().lookupDisease(it) }.map {
                val pcuCode = it.getPcuCode()
                val hCode = it.getHcode()
                check(pcuCode != null) { "พบรหัส pcucodeperson จากข้อมูล person = null" }
                check(hCode != null) { "พบรหัส hcode จากข้อมูล person = null" }
                it.houseId = houseId["$pcuCode:$hCode"] ?: ""
                it
            }
            return persons
        }

    override val cloud: List<Person> = cloudCache

    /**
     * @return pcuCode:hcode,houseId
     */
    private fun cacheHouseId(): SortedMap<String, String> {
        return houseManage.cloud.map {
            val pcuCode = it.link?.keys?.get("pcucode")?.toString()?.trim()
            val hCode = it.link?.keys?.get("hcode")?.toString()?.trim()
            "$pcuCode:$hCode" to it.id
        }.toMap().toSortedMap()
    }

    private val lock = Any()
    override fun sync(force: Boolean): List<Entity>? {
        return synchronized(lock) {
            syncSync()
        }
    }

    private fun syncSync(): List<Entity>? {
        val proSync: ProSync<Person> = V1ProSync()

        // ดูว่ามีอะไร update ไหม
        run {
            val listUpdate = arrayListOf<Person>()
            proSync.update(local, cloudCache) { person ->
                object : ProSync.UpdateFunc<Person> {
                    override val identity: String = person.getIdentity()
                    override val unixTime: Long = person.timestamp.millis
                    override fun updateTo(item: Person) {
                        listUpdate.add(person.copy(item.id))
                    }
                }
            }
            if (listUpdate.isNotEmpty()) {
                logger.info { "Person update size:${listUpdate.size}" }
                synchronized(file) {
                    personApi.updatePersons(listUpdate) `อัพเดทไปยัง` cloudCache
                    ffcFileSave(file, cloudCache)
                }
            }
        }

        // ดูว่ามีอะไรใหม่สร้างบน cloud ไหม
        run {
            val listCreateDataToCloud = arrayListOf<Person>()
            proSync.createNewDataInB(local, cloudCache) { person ->
                object : ProSync.CreateFunc {
                    override val identity: String = person.getIdentity()
                    override val bIsDelete: Boolean = false
                    override fun createInB() {
                        listCreateDataToCloud.add(person)
                    }
                }
            }

            if (listCreateDataToCloud.isNotEmpty()) {
                logger.info { "Person create size:${listCreateDataToCloud.size}" }
                synchronized(file) {
                    if (cloudCache.isEmpty())
                        personApi.createPerson(listCreateDataToCloud, {}, true) `อัพเดทไปยัง` cloudCache
                    else
                        personApi.createPerson(listCreateDataToCloud, {}, false) `อัพเดทไปยัง` cloudCache
                    ffcFileSave(file, cloudCache)
                }
            }
        }

        // TODO ลบข้อมูลบน cloud ยังไม่ได้พัฒนา
        run {
        }
        return cloudCache.toList()
    }

    override fun findPersonIdInCloud(pcuCode: String, pid: String): Person? {
        return cloudCache.find {
            it.getPcuCode() == pcuCode && it.getPid() == pid
        }
    }

    private fun Person.getIdentity(): String = "${getPcuCode()}:${getPid()}"
}

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

package ffc.airsync.api.house

import ffc.airsync.Main
import ffc.airsync.api.person.getPersons
import ffc.airsync.api.sync.ProSync
import ffc.airsync.api.sync.V1ProSync
import ffc.airsync.db.DatabaseDao
import ffc.airsync.utils.`อัพเดทไปยัง`
import ffc.airsync.utils.ffcFileLoad
import ffc.airsync.utils.ffcFileSave
import ffc.airsync.utils.getLogger
import ffc.entity.Entity
import ffc.entity.Person
import ffc.entity.Village
import ffc.entity.copy
import ffc.entity.place.House
import java.io.File
import java.util.SortedSet

class HouseManage(
    val dao: DatabaseDao = Main.instant.dao,
    private val func: () -> Func
) : HouseInterface {

    private val houseApi: HouseApi = HouseServiceApi()
    private var cloudCache = arrayListOf<House>()
    private val file = File("data", "house.json")
    private val logger = getLogger(this)

    init {
        cloudCache.addAll(ffcFileLoad<House>(file))
    }

    interface Func {
        fun villageLookup(villageCode: String): Village?
    }

    override val local: List<House>
        get() {
            val houseFromDatabase = dao.getHouse(lookupVillage = { func().villageLookup(it) })
            logger.info { "House from jhcisdb size:${houseFromDatabase.size}" }
            val listHouseChronic = getPersons().findHouseIsChronic()
            return houseFromDatabase.map {
                val hcode = getHcode(it)
                val pcuCode = getPcuCode(it)
                it.haveChronic = listHouseChronic.contains("$pcuCode:$hcode")
                it
            }
        }

    private fun getPcuCode(it: House) = it.link!!.keys["pcucode"] as String

    private fun getHcode(it: House) = it.link!!.keys["hcode"] as String

    override val cloud: List<House> = cloudCache

    private val lock = Any()
    override fun sync(force: Boolean): List<Entity>? {
        return synchronized(lock) {
            syncSync()
        }
    }

    private fun syncSync(): List<House> {
        val proSync: ProSync<House> = V1ProSync()

        // ดูว่ามีอะไรอัพเดทไหม
        run {
            val listUpdate = arrayListOf<House>()
            proSync.update(local, cloudCache) { house ->
                object : ProSync.UpdateFunc<House> {
                    override val unixTime: Long = house.timestamp.millis
                    override val identity: String = house.getIdentity()
                    override fun updateTo(item: House) {
                        listUpdate.add(house.copy(item.id))
                    }
                }
            }
            if (listUpdate.isNotEmpty()) {
                logger.info { "House update size:${listUpdate.size}" }
                synchronized(file) {
                    houseApi.update(listUpdate) `อัพเดทไปยัง` cloudCache
                    ffcFileSave(file, cloudCache)
                }
            }
        }

        // ดูว่ามีอะไรต้องสร้างใหม่บน cloud ไหม
        run {
            val listCreateDataToCloud = arrayListOf<House>()
            proSync.createNewDataInB(local, cloudCache) { house ->
                object : ProSync.CreateFunc {
                    override val identity: String = house.getIdentity()
                    override val bIsDelete: Boolean = false
                    override fun createInB() {
                        listCreateDataToCloud.add(house)
                    }
                }
            }
            if (listCreateDataToCloud.isNotEmpty()) {
                logger.info { "House create size:${listCreateDataToCloud.size}" }
                synchronized(file) {
                    if (cloudCache.isEmpty())
                        houseApi.createHouse(listCreateDataToCloud, {}, true) `อัพเดทไปยัง` cloudCache
                    else
                        houseApi.createHouse(listCreateDataToCloud, {}, false) `อัพเดทไปยัง` cloudCache
                    ffcFileSave(file, cloudCache)
                }
            }
        }
        // TODO ลบข้อมูลบน cloud ยังไม่ได้พัฒนา
        run {
        }
        return cloudCache.toList()
    }

    override fun directUpdateCloudData(list: List<House>) {
        val update = houseApi.update(list)
        if (update.isNotEmpty()) {
            synchronized(file) {
                update `อัพเดทไปยัง` cloudCache
                ffcFileSave(file, cloudCache)
            }
        }
    }

    override fun sync(id: String) {
        val get = houseApi.get(id) ?: return
        dao.upateHouse(get)
        synchronized(file) {
            listOf(get) `อัพเดทไปยัง` cloudCache
            ffcFileSave(file, cloudCache)
        }
    }

    private fun List<Person>.findHouseIsChronic(): SortedSet<String> {
        val output = HashSet<String>()
        forEach {
            if (!it.haveChronic) return@forEach
            val hCode = it.link!!.keys["hcode"]?.toString()?.trim()
            if (hCode == "1") return@forEach // ไม่เอาบ้านนอกเขต
            val pcuCoce = it.link!!.keys["pcucodeperson"]?.toString()?.trim()
            output.add("$pcuCoce:$hCode")
        }
        return output.toSortedSet()
    }

    private fun House.getIdentity(): String = "${getPcuCode(this)}:${getHcode(this)}"
}

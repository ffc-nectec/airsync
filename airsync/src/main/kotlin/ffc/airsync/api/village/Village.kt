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

import ffc.airsync.Main
import ffc.airsync.utils.checkNewDataCreate
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.airsync.villageApi
import ffc.airsync.villages
import ffc.entity.Village
import ffc.entity.gson.toJson

val VILLAGELOOKUP = { jVillageId: String ->
    val find = villages.find { it.link!!.keys["villcode"].toString() == jVillageId }
    if (find == null) {
        villages.initSync()
        villages.find { it.link!!.keys["villcode"].toString() == jVillageId }!!
    } else
        find
}

/**
 * ดึงข้อมูลรายการหมู่บ้านจาก jhcisdb
 */
fun List<Village>.getVillage() = Main.instant.dao.getVillage()

fun ArrayList<Village>.initSync() {
    this.lock {
        val cacheFile = arrayListOf<Village>().apply { addAll(load()) }
        val jhcisVillage = cacheFile.getVillage()

        clear()
        if (cacheFile.isEmpty()) {
            addAll(villageApi.toCloud(jhcisVillage))
            save()
        } else {
            addAll(cacheFile)
            checkNewDataCreate(jhcisVillage, cacheFile, { jhcis, cloud -> jhcis.name == cloud.name }) {
                getLogger(this).info { "Create new village ${it.toJson()}" }
                addAll(villageApi.toCloud(it, false))
                save()
            }
        }
    }
}

private const val villageLock = "lock"

fun List<Village>.lock(f: () -> Unit) {
    synchronized(villageLock) {
        f()
    }
}

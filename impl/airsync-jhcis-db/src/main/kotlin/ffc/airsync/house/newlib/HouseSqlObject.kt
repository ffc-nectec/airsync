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

package ffc.airsync.house.newlib

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime

internal object HouseSqlObject : Table("house") {
    val pcucode = char("pcucode", 5).nullable()
    val hcode = integer("hcode")
    val villcode = varchar("villcode", 8)
    val hid = varchar("hid", 18).nullable()
    val hno = varchar("hno", 120).nullable()
    val road = varchar("road", 257).nullable()
    val pcucodepersonvola = char("pcucodepersonvola", 5).nullable()
    val pidvola = integer("pidvola").nullable()
    val xgis = varchar("xgis", 55).nullable()
    val ygis = varchar("ygis", 55).nullable()
    val housepic = blob("housepic").nullable()
    val usernamedoc = varchar("usernamedoc", 35).nullable()
    val dateupdate = datetime("dateupdate")
}

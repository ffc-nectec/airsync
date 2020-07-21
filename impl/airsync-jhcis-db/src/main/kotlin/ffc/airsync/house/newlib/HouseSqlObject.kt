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

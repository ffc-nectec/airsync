package ffc.airsync.api.chronic

import ffc.airsync.Main
import ffc.airsync.db.DatabaseDao
import ffc.entity.healthcare.Chronic

fun Chronic.gets(dao: DatabaseDao = Main.instant.createDatabaseDao()): List<Chronic> {
    return dao.getChronic()
}

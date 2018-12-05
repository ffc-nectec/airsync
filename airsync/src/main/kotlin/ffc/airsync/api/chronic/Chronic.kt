package ffc.airsync.api.chronic

import ffc.airsync.Main
import ffc.airsync.db.DatabaseDao
import ffc.entity.healthcare.Chronic

fun Chronics(dao: DatabaseDao = Main.instant.dao): List<Chronic> {
    return dao.getChronic()
}

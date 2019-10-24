package ffc.airsync

import ffc.airsync.api.house.initSync
import ffc.airsync.api.person.SyncPerson
import ffc.airsync.api.person.initSync
import ffc.airsync.api.template.TemplateInit
import ffc.airsync.api.user.initSync
import ffc.airsync.api.village.initSync
import ffc.airsync.db.DatabaseDao
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.syncCloud

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
                } catch (ignore: Exception) {
                    ignore.printStackTrace()
                }

                Thread.sleep(60000)
            }
        }
    }

    private fun autoSyncToCloud(): Thread {
        return Thread {
            val hour = 60000L * 60L
            while (true) {
                try {
                    logger.info("Sync template")
                    runCatching { TemplateInit() }
                    logger.info("Sync user")
                    runCatching { users.initSync() }
                    logger.info("Sync village")
                    runCatching { villages.initSync() }
                    runCatching {
                        logger.info("Sync person")
                        val syncPerson = SyncPerson()
                        val jhcisDbPerson = syncPerson.prePersonProcess()
                        houses.initSync(jhcisDbPerson) {}
                        logger.info("Sync house")
                        persons.initSync(houses, jhcisDbPerson) {}
                    }
                } catch (ignore: Exception) {
                    ignore.printStackTrace()
                }
                Thread.sleep(hour)
            }
        }
    }
}

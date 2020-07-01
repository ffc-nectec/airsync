package ffc.airsync

import ffc.airsync.api.house.initSync
import ffc.airsync.api.person.SyncPerson
import ffc.airsync.api.person.initSync
import ffc.airsync.api.template.TemplateInit
import ffc.airsync.api.village.initSync
import ffc.airsync.db.DatabaseDao
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.syncCloud
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
                } catch (ignore: Exception) {
                    ignore.printStackTrace()
                    logger.error(ignore.message!!, ignore)
                }

                Thread.sleep(60000)
            }
        }
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
                        houses.initSync(jhcisDbPerson) {}
                        logger.info("Sync house")
                        persons.initSync(houses, jhcisDbPerson) {}
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

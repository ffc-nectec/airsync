package ffc.airsync

import ffc.airsync.db.DatabaseDao
import ffc.airsync.utils.syncCloud

class SetupAutoSync(val dao: DatabaseDao) {

    init {
        setUpAutoSync()
    }

    private fun setUpAutoSync() {
        Thread {
            while (true) {
                try {
                    syncCloud.sync(dao)
                } catch (ignore: Exception) {
                }

                Thread.sleep(60000)
            }
        }.start()
    }
}

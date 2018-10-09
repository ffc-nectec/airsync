package ffc.airsync

import ffc.airsync.db.DatabaseDao

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

                Thread.sleep(5000)
            }
        }.start()
    }
}

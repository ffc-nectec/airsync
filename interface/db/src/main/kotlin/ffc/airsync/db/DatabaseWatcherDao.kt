package ffc.airsync.db

interface DatabaseWatcherDao {
    var isShutdown: Boolean
    fun start()
}

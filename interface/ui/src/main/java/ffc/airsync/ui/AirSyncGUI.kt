package ffc.airsync.ui

typealias KEY = String

interface AirSyncGUI {
    fun set(data: Pair<KEY, Any>)
    fun remove(key: KEY)
    fun hideWindows()
    fun showWIndows()

    data class ProgressData(val current: Int, val all: Int)
}

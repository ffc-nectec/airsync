package ffc.airsync.ui

typealias KEY = String

interface AirSyncGUI {
    fun set(data: Pair<KEY, Any>)
    fun remove(key: KEY)
    fun hideWindows()
    fun showWIndows()
    fun switchhHideShow()
    fun setHeader(string: String)

    data class ProgressData(val current: Int, val max: Int, val message: String? = null)
}

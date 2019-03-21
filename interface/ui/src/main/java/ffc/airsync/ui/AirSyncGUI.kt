package ffc.airsync.ui

typealias KEY = String

interface AirSyncGUI {
    fun set(data: Pair<KEY, Any>)
    fun remove(key: KEY)
    fun hideWindows()
    fun showWIndows()
    fun switchhHideShow()
    fun setHeader(string: String)
    fun setLocation(x: Int, y: Int)
    fun createRightClick(x: Int, y: Int)
    fun hideRightClick()
    var enableSyncButton: Boolean

    data class ProgressData(val current: Int, val max: Int, val message: String? = null)
    data class CheckData(val message: String, val type: MESSAGE_TYPE = MESSAGE_TYPE.OK)

    enum class MESSAGE_TYPE {
        OK, ERROR
    }
}
